package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse createBooking(BookingDto bookingDto, long userId);

    BookingDtoResponse updateBooking(long bookingId, boolean approved, long userId);

    BookingDtoResponse getBooking(long bookingId, long userId);

    List<BookingDtoResponse> getBookings(String state, long userId, int from, int size);

    List<BookingDtoResponse> getItemsOwnerBookings(String state, long userId, int from, int size);
}
