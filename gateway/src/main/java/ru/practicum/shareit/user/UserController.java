package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(
            @RequestBody @Valid UserRequestDto requestDto) {
        log.info("Create user {}", requestDto);
        return userClient.createUser(requestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(
            @RequestBody UserRequestDto requestDto,
            @PathVariable long id) {
        log.info("Update user {}", id);
        return userClient.updateUser(id, requestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(
            @PathVariable long id) {
        log.info("Get user {}", id);
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(
            @PathVariable long id) {
        log.info("Delete user {}", id);
        return userClient.deleteUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get users");
        return userClient.getAllUsers();
    }
}
