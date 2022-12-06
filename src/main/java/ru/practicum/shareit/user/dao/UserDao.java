package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserDao {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long id);

    UserDto getUser(long id);

    UserDto deleteUser(long id);

    List<UserDto> getAllUsers();

}
