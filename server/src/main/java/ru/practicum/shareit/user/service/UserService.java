package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long id);

    UserDto getUser(long id);

    UserDto deleteUser(long id);

    List<UserDto> getAllUsers();
}
