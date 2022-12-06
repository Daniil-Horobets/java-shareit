package ru.practicum.shareit.exceptions;

public class ItemNameBlankException extends RuntimeException {
    public ItemNameBlankException(String message) {
        super(message);
    }
}
