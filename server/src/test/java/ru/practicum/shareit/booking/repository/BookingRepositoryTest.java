package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;


    private LocalDateTime now;
    private LocalDateTime start;
    private LocalDateTime end;
    private User user1;
    private User user2;
    private Item item1;
    private Booking booking1;


    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        user1 = new User(1, "User1 name", "user1@mail.com");
        user2 = new User(2, "User2 name", "user2@mail.com");
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(null)
                .build();
        item1 = itemRepository.save(item1);

        booking1 = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        booking1 = bookingRepository.save(booking1);
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findByBookerIdOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(
                user2.getId(), PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        booking1.setStart(start.minusDays(5));
        booking1.setEnd(end.plusDays(5));
        bookingRepository.save(booking1);
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                user2.getId(), now, now, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartDescTest() {
        booking1.setStart(start.minusDays(5));
        booking1.setEnd(end.minusDays(3));
        bookingRepository.save(booking1);
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                user2.getId(), now, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByBookerIdAndStartAfterOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                user2.getId(), now, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                user2.getId(), Status.WAITING, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByItemOwnerIdOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(
                user1.getId(), PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        booking1.setStart(start.minusDays(5));
        booking1.setEnd(end.plusDays(5));
        bookingRepository.save(booking1);
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                user1.getId(), now, now, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByItemOwnerIdAndEndBeforeOrderByStartDescTest() {
        booking1.setStart(start.minusDays(5));
        booking1.setEnd(end.minusDays(3));
        bookingRepository.save(booking1);
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                user1.getId(), now, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);

    }

    @Test
    void findByItemOwnerIdAndStartAfterOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                user1.getId(), now, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                user1.getId(), Status.WAITING, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByItemIdAndEndBeforeOrderByEndDescTest() {
        booking1.setStart(start.minusDays(5));
        booking1.setEnd(end.minusDays(3));
        bookingRepository.save(booking1);
        Booking booking = bookingRepository.findByItemIdAndEndBeforeOrderByEndDesc(
                item1.getId(), now).orElseThrow(() -> new BookingNotFoundException(ErrorHandler.BOOKING_NOT_FOUND));

        assertEquals(booking1, booking);
    }

    @Test
    void findByItemIdAndStartAfterOrderByEndAscTest() {
        Booking booking = bookingRepository.findByItemIdAndStartAfterOrderByEndAsc(
                item1.getId(), now).orElseThrow(() -> new BookingNotFoundException(ErrorHandler.BOOKING_NOT_FOUND));

        assertEquals(booking1, booking);
    }

    @Test
    void findByBookerIdAndItemIdAndStatusAndEndBeforeTest() {
        booking1.setStart(start.minusDays(5));
        booking1.setEnd(end.minusDays(3));
        bookingRepository.save(booking1);
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                user2.getId(), item1.getId(), Status.WAITING, now);

        assertEquals(List.of(booking1), bookings);
    }
}