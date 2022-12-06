package ru.practicum.shareit.exceptions;

public class UserEmailBlankException extends RuntimeException {
    public UserEmailBlankException(String message) {
        super(message);
    }
}
