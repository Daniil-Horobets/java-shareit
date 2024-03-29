package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private UserDto user1Dto;

    @BeforeEach
    void beforeEach() {
        user1Dto = UserMapper.toUserDto(new User(1, "User1 name", "user1@mail.com"));
    }

    @Test
    void createUserTest() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(user1Dto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1Dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1Dto)));
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(any(UserDto.class), anyLong()))
                .thenReturn(user1Dto);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(user1Dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1Dto)));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(user1Dto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1Dto)));
    }

    @Test
    void deleteUserTest() throws Exception {
        when(userService.deleteUser(anyLong()))
                .thenReturn(user1Dto);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1Dto)));
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(user1Dto));

        mockMvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(user1Dto))));
    }
}