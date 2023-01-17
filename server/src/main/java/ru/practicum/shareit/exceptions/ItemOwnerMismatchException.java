package ru.practicum.shareit.exceptions;

public class ItemOwnerMismatchException extends RuntimeException {
    public ItemOwnerMismatchException(String message) {
        super(message);
    }
}
