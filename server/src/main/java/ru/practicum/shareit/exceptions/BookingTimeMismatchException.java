package ru.practicum.shareit.exceptions;

public class BookingTimeMismatchException extends RuntimeException {
    public BookingTimeMismatchException(String message) {
        super(message);
    }
}
