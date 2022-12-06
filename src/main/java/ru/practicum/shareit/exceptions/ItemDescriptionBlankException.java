package ru.practicum.shareit.exceptions;

public class ItemDescriptionBlankException extends RuntimeException {
    public ItemDescriptionBlankException(String message) {
        super(message);
    }
}
