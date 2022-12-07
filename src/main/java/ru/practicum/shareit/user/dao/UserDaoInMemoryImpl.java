package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.UserEmailExistsException;
import ru.practicum.shareit.exceptions.UserEmailMismatchException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class UserDaoInMemoryImpl implements UserDao {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> usersByEmails = new HashMap<>();
    private long idCounter = 0;
    // RFC5322 email format
    private static final Pattern RFC5322_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");


    @Override
    public UserDto createUser(UserDto userDto) {
        validateUser(userDto);
        userDto.setId(++idCounter);
        User user = new User(userDto.getId(), userDto.getName(), userDto.getEmail());
        users.put(user.getId(), user);
        usersByEmails.put(user.getEmail(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        User initialUser = users.get(id);
        User user = new User();
        user.setId(id);
        if (userDto.getEmail() != null) {
            validateUser(userDto);
            usersByEmails.remove(initialUser.getEmail());
            user.setEmail(userDto.getEmail());
        } else {
            user.setEmail(initialUser.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        } else {
            user.setName(initialUser.getName());
        }
        users.put(id, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapper.toUserDto(users.get(id));
    }

    @Override
    public UserDto deleteUser(long id) {
        User user = users.get(id);
        users.remove(id);
        usersByEmails.remove(user.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return new ArrayList<>(
                users.values()).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList()
                );
    }

    private void validateUser(UserDto userDto) {
        if (usersByEmails.containsKey(userDto.getEmail())) {
            throw new UserEmailExistsException("User with this email already exists");
        } else if (
                !RFC5322_PATTERN
                        .matcher(userDto.getEmail())
                        .matches()
        ) {
            throw new UserEmailMismatchException("User email format is incorrect");
        }
    }
}
