package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private UserDto user1Dto;
    private UserDto user2Dto;
    private ItemDto item1Dto;
    private ItemDtoBooking item1DtoBooking;
    private CommentDto comment1Dto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        User user1 = new User(1, "User1 name", "user1@mail.com");
        user1Dto = UserMapper.toUserDto(user1);
        User user2 = new User(2, "User2 name", "user2@mail.com");
        user2Dto = UserMapper.toUserDto(user2);

        Item item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(null)
                .build();
        item1Dto = ItemMapper.toItemDto(item1);
        item1DtoBooking = ItemMapper.toItemDtoBooking(item1);

        Comment comment1 = Comment.builder()
                .id(1)
                .text("Comment1 text")
                .item(item1)
                .author(user2)
                .created(now)
                .build();
        comment1Dto = CommentMapper.toCommentDto(comment1);
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(any(ItemDto.class), anyLong()))
                .thenReturn(item1Dto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item1Dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(item1Dto)));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(item1Dto);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(item1Dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(item1Dto)));
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(item1DtoBooking);

        mockMvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(item1DtoBooking)));
    }

    @Test
    void getItemsTest() throws Exception {
        when(itemService.getUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(item1DtoBooking));

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(item1DtoBooking))));
    }

    @Test
    void searchItemTest() throws Exception {
        when(itemService.findItem(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item1Dto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item1")
                        .header(USER_ID_HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(item1Dto))));
    }

    @Test
    void createComment() throws Exception {
        when(itemService.createComment(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(comment1Dto);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment1Dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user2Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(comment1Dto)));
    }
}