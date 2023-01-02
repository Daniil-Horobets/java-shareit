package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDtoResponse createBooking(BookingDto bookingDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorHandler.USER_NOT_FOUND));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(ErrorHandler.ITEM_NOT_FOUND));
        if (!item.getAvailable()) {
            throw new BookingStatusMismatchException("Item is not available");
        }
        if (item.getOwner().equals(user)) {
            throw new ItemOwnerMismatchException("Owner can not book his own items");
        }
        LocalDateTime now = LocalDateTime.now();
        boolean isTimeNotCorrect = bookingDto.getStart().isBefore(now) ||
                bookingDto.getEnd().isBefore(now) ||
                bookingDto.getEnd().isBefore(bookingDto.getStart());
        if (isTimeNotCorrect) {
            throw new BookingTimeMismatchException(ErrorHandler.BOOKING_TIME_MISMATCH);
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse updateBooking(long bookingId, boolean approved, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(ErrorHandler.BOOKING_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorHandler.USER_NOT_FOUND));
        if (!user.equals(booking.getItem().getOwner())) {
            throw new ItemOwnerMismatchException(ErrorHandler.ITEM_OWNER_MISMATCH);
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BookingStatusMismatchException("Current booking status is " + booking.getStatus() +
                    ", but should be " + Status.WAITING);
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse getBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(ErrorHandler.BOOKING_NOT_FOUND));
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorHandler.USER_NOT_FOUND));
        boolean isOwnerOrBooker = booking.getBooker().getId() == userId ||
                booking.getItem().getOwner().getId() == userId;
        if (!isOwnerOrBooker) {
            throw new ItemOwnerMismatchException("User is not a booker or item owner");
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> getBookings(String state, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorHandler.USER_NOT_FOUND));
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return bookingsToBookingDtoResponses(
                        bookingRepository.findByBookerIdOrderByStartDesc(
                            userId
                        ));
            case "CURRENT":
                return bookingsToBookingDtoResponses(
                        bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                            userId,
                            now,
                            now
                        ));
            case "PAST":
                return bookingsToBookingDtoResponses(
                        bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                            userId,
                            now
                        ));
            case "FUTURE":
                return bookingsToBookingDtoResponses(
                        bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                            userId,
                            now
                        ));
            case "WAITING":
                return bookingsToBookingDtoResponses(
                        bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                            userId,
                            Status.WAITING
                        ));
            case "REJECTED":
                return bookingsToBookingDtoResponses(
                        bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                            userId,
                            Status.REJECTED
                        ));
            default:
                throw new BookingStatusMismatchException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDtoResponse> getItemsOwnerBookings(String state, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorHandler.USER_NOT_FOUND));
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return bookingsToBookingDtoResponses(
                        bookingRepository.findByItemOwnerIdOrderByStartDesc(
                            userId
                        ));
            case "CURRENT":
                return bookingsToBookingDtoResponses(
                        bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                            userId,
                            now,
                            now
                        ));
            case "PAST":
                return bookingsToBookingDtoResponses(
                        bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                            userId,
                            now
                        ));
            case "FUTURE":
                return bookingsToBookingDtoResponses(
                        bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                            userId,
                            now
                        ));
            case "WAITING":
                return bookingsToBookingDtoResponses(
                        bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                            userId,
                            Status.WAITING
                        ));
            case "REJECTED":
                return bookingsToBookingDtoResponses(
                        bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                            userId,
                            Status.REJECTED
                        ));
            default:
                throw new BookingStatusMismatchException("Unknown state: " + state);
        }
    }

    private List<BookingDtoResponse> bookingsToBookingDtoResponses(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }
}
