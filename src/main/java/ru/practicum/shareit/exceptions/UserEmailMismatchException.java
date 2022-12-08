package ru.practicum.shareit.exceptions;

public class UserEmailMismatchException extends RuntimeException {
    public UserEmailMismatchException(String message) {
        super(message);
    }
}
