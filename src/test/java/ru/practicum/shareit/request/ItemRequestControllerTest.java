package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private UserDto user1Dto;
    private ItemRequestDto itemRequest1Dto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        User user1 = new User(1, "User1 name", "user1@mail.com");
        user1Dto = UserMapper.toUserDto(user1);

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1)
                .description("ItemRequest1 description")
                .requestor(user1)
                .created(now)
                .build();
        itemRequest1Dto = ItemRequestMapper.toItemRequestDto(itemRequest1);
    }

    @Test
    void createItemRequestTest() throws Exception {
        when(itemRequestService.createItemRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequest1Dto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequest1Dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequest1Dto)));
    }

    @Test
    void getUserItemRequestsTest() throws Exception {
        when(itemRequestService.getUserItemRequests(anyLong()))
                .thenReturn(List.of(itemRequest1Dto));

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequest1Dto))));
    }

    @Test
    void getItemRequestsTest() throws Exception {
        when(itemRequestService.getItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequest1Dto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequest1Dto))));
    }

    @Test
    void getItemRequestTest() throws Exception {
        when(itemRequestService.getItemRequest(anyLong(), anyLong()))
                .thenReturn(itemRequest1Dto);

        mockMvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequest1Dto)));
    }
}