package ru.practicum.shareit.exceptions;

public class BookingStatusMismatchException extends RuntimeException {
    public BookingStatusMismatchException(String message) {
        super(message);
    }
}
