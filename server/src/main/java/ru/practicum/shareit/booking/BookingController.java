package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoResponse createBooking(
            @RequestBody BookingDto bookingDto,
            @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBooking(
            @PathVariable long bookingId,
            @RequestParam boolean approved,
            @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.updateBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(
            @PathVariable long bookingId,
            @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getBookings(
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getItemsOwnerBookings(
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getItemsOwnerBookings(state, userId, from, size);
    }
}
