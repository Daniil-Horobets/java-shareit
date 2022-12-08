package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserDto createUser(UserDto user) {
        return userDao.createUser(user);
    }

    public UserDto updateUser(UserDto user, long id) {
        return userDao.updateUser(user, id);
    }

    public UserDto getUser(long id) {
        return userDao.getUser(id);
    }

    public UserDto deleteUser(long id) {
        return userDao.deleteUser(id);
    }

    public List<UserDto> getAllUsers() {
        return userDao.getAllUsers();
    }
}
