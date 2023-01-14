package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private LocalDateTime start;
    private LocalDateTime end;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Booking booking1;

    @BeforeEach
    void beforeEach() {

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);

        user1 = new User(1, "User1 name", "user1@mail.com");
        user2 = new User(2, "User2 name", "user2@mail.com");
        user3 = new User(3, "User3 name", "user3@mail.com");

        item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(null)
                .build();

        booking1 = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(
                BookingMapper.toBookingDto(booking1),
                user2.getId());

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(Status.WAITING, bookingDtoResponse.getStatus());
    }

    @Test
    void createBookingWithBookerAsOwnerUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        ItemOwnerMismatchException exception = assertThrows(ItemOwnerMismatchException.class,
                () -> bookingService.createBooking(
                        BookingMapper.toBookingDto(booking1),
                        user1.getId()));

        assertEquals("Owner can not book his own items", exception.getMessage());
    }

    @Test
    void createBookingOnNotAvailableItemTest() {
        item1.setAvailable(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingStatusMismatchException exception = assertThrows(BookingStatusMismatchException.class,
                () -> bookingService.createBooking(
                        BookingMapper.toBookingDto(booking1),
                        user1.getId()));

        assertEquals("Item is not available", exception.getMessage());
    }

    @Test
    void createBookingWithWrongTimeTest() {
        booking1.setStart(LocalDateTime.now().minusDays(3));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingTimeMismatchException exception = assertThrows(BookingTimeMismatchException.class,
                () -> bookingService.createBooking(
                        BookingMapper.toBookingDto(booking1),
                        user2.getId()));

        assertEquals("Booking time is incorrect", exception.getMessage());
    }

    @Test
    void createBookingOnNotExistingItemTest() {
        booking1.setStart(LocalDateTime.now().minusDays(3));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.createBooking(
                        BookingMapper.toBookingDto(booking1),
                        user2.getId()));

        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void updateBookingTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDtoResponse bookingDtoResponse = bookingService.updateBooking(
                booking1.getId(),
                true,
                user1.getId());

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(Status.APPROVED, bookingDtoResponse.getStatus());
    }

    @Test
    void updateBookingWithWrongIdTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateBooking(
                        booking1.getId(),
                        true,
                        user2.getId()));

        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void updateBookingFromWrongUserTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        ItemOwnerMismatchException exception = assertThrows(ItemOwnerMismatchException.class,
                () -> bookingService.updateBooking(
                        booking1.getId(),
                        true,
                        user2.getId()));

        assertEquals("User not an item owner", exception.getMessage());
    }

    @Test
    void updateBookingWithWrongStatusTest() {
        booking1.setStatus(Status.REJECTED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingStatusMismatchException exception = assertThrows(BookingStatusMismatchException.class,
                () -> bookingService.updateBooking(
                        booking1.getId(),
                        true,
                        user1.getId()));

        assertEquals("Current booking status is " + booking1.getStatus() +
                ", but should be " + Status.WAITING, exception.getMessage());
    }

    @Test
    void updateBookingRejectTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDtoResponse bookingDtoResponse = bookingService.updateBooking(
                booking1.getId(),
                false,
                user1.getId());

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(Status.REJECTED, bookingDtoResponse.getStatus());
    }

    @Test
    void getBookingTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        BookingDtoResponse bookingDtoResponse = bookingService.getBooking(
                booking1.getId(),
                user1.getId());

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(Status.WAITING, bookingDtoResponse.getStatus());
    }

    @Test
    void getBookingFromWrongUserTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));

        ItemOwnerMismatchException exception = assertThrows(ItemOwnerMismatchException.class,
                () -> bookingService.getBooking(
                        booking1.getId(),
                        user3.getId()));

        assertEquals("User is not a booker or item owner", exception.getMessage());
    }

    @Test
    void getBookingsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getBookings("ALL",
                user1.getId(),
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsCurrentStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getBookings("CURRENT",
                user1.getId(),
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsPastStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getBookings("PAST",
                user1.getId(),
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsFutureStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getBookings("FUTURE",
                user1.getId(),
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                anyLong(),
                any(Status.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getBookings("WAITING",
                user1.getId(),
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsRejectedStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                anyLong(),
                any(Status.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getBookings("REJECTED",
                user1.getId(),
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsUnknownStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        BookingStatusMismatchException exception = assertThrows(BookingStatusMismatchException.class,
                () -> bookingService.getBookings("UNKNOWN",
                        user1.getId(),
                        0,
                        10));

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void getItemsOwnerBookingsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getItemsOwnerBookings("ALL",
                user1.getId(),
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsCurrentStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getItemsOwnerBookings("CURRENT",
                user1.getId(),
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsPastStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getItemsOwnerBookings("PAST",
                user1.getId(),
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsFutureStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getItemsOwnerBookings("FUTURE",
                user1.getId(),
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                anyLong(),
                any(Status.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getItemsOwnerBookings("WAITING",
                user1.getId(),
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsRejectedStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                anyLong(),
                any(Status.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getItemsOwnerBookings("REJECTED",
                user1.getId(),
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsUnknownStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        BookingStatusMismatchException exception = assertThrows(BookingStatusMismatchException.class,
                () -> bookingService.getItemsOwnerBookings("UNKNOWN",
                        user1.getId(),
                        0,
                        10));

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }
}