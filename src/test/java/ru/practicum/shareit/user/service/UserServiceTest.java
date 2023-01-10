package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exceptions.UserEmailMismatchException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    private User user1;


    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "User1 name", "user1@mail.com");
    }

    @Test
    void createUserTest() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);

        UserDto userDto = userService.createUser(
                UserMapper.toUserDto(user1));

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void createUserWithEmailInWrongFormatTest() {
        user1.setEmail("user1mail");

        UserEmailMismatchException exception = assertThrows(UserEmailMismatchException.class,
                () -> userService.createUser(UserMapper.toUserDto(user1)));

        assertEquals("User email format is incorrect", exception.getMessage());
    }

    @Test
    void updateUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);

        UserDto userDto = userService.updateUser(
                UserMapper.toUserDto(user1), user1.getId());

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void getUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        UserDto userDto = userService.getUser(user1.getId());

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void getUserWrongIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUser(user1.getId()));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void deleteUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        UserDto userDto = userService.deleteUser(user1.getId());

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void getAllUsersTest() {
        when(userRepository.findAll())
                .thenReturn(List.of(user1));

        List<UserDto> userDtos = userService.getAllUsers();

        assertEquals(1, userDtos.size());
        assertEquals(1, userDtos.get(0).getId());
        assertEquals("User1 name", userDtos.get(0).getName());
        assertEquals("user1@mail.com", userDtos.get(0).getEmail());
    }
}