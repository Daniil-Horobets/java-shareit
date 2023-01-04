package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.exceptions.UserEmailMismatchException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private static final Pattern RFC5322_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    );

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserDto userDto) {
        validateUserEmail(userDto);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    public UserDto updateUser(UserDto userDto, long id) {
        UserDto initialUser = getUser(id);
        UserDto user = new UserDto();
        user.setId(id);
        if (userDto.getEmail() != null) {
            validateUserEmail(userDto);
            user.setEmail(userDto.getEmail());
        } else {
            user.setEmail(initialUser.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        } else {
            user.setName(initialUser.getName());
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(user)));
    }

    public UserDto getUser(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return UserMapper.toUserDto(optionalUser.get());
        } else {
            throw new UserNotFoundException(ErrorHandler.USER_NOT_FOUND);
        }
    }

    public UserDto deleteUser(long id) {
        UserDto userToReturn = getUser(id);
        userRepository.deleteById(id);
        return userToReturn;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void validateUserEmail(UserDto userDto) {
        if (!RFC5322_PATTERN
                .matcher(userDto.getEmail())
                .matches()) {
            throw new UserEmailMismatchException(ErrorHandler.USER_EMAIL_MISMATCH);
        }
    }
}
