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
        boolean isTimeNotCorrect = bookingDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingDto.getEnd().isBefore(LocalDateTime.now()) ||
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
        switch (state) {
            case "ALL":
                return bookingRepository.findByBooker_IdOrderByStartDesc(
                        userId
                    ).stream()
                    .map(BookingMapper::toBookingDtoResponse)
                    .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                    ).stream()
                    .map(BookingMapper::toBookingDtoResponse)
                    .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(
                        userId,
                        LocalDateTime.now()
                    ).stream()
                    .map(BookingMapper::toBookingDtoResponse)
                    .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now()
                    ).stream()
                    .map(BookingMapper::toBookingDtoResponse)
                    .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(
                        userId,
                        Status.WAITING
                    ).stream()
                    .map(BookingMapper::toBookingDtoResponse)
                    .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(
                        userId,
                        Status.REJECTED
                    ).stream()
                    .map(BookingMapper::toBookingDtoResponse)
                    .collect(Collectors.toList());
            default:
                throw new BookingStatusMismatchException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDtoResponse> getItemsOwnerBookings(String state, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorHandler.USER_NOT_FOUND));
        switch (state) {
            case "ALL":
                return bookingRepository.findByItem_Owner_IdOrderByStartDesc(
                        userId
                    ).stream()
                    .map(BookingMapper::toBookingDtoResponse)
                    .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                    ).stream()
                    .map(BookingMapper::toBookingDtoResponse)
                    .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(
                        userId,
                        LocalDateTime.now()
                    ).stream()
                    .map(BookingMapper::toBookingDtoResponse)
                    .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now()
                    ).stream()
                    .map(BookingMapper::toBookingDtoResponse)
                    .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(
                        userId,
                        Status.WAITING
                    ).stream()
                    .map(BookingMapper::toBookingDtoResponse)
                    .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(
                        userId,
                        Status.REJECTED
                    ).stream()
                    .map(BookingMapper::toBookingDtoResponse)
                    .collect(Collectors.toList());
            default:
                throw new BookingStatusMismatchException("Unknown state: " + state);
        }
    }
}
