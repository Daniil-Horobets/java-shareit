package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByStartDesc(
            Long userId);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end);

    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(
            Long userId,
            LocalDateTime end);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(
            Long userId,
            LocalDateTime start);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(
            Long userId,
            Status status);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(
            Long userId);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(
            Long userId,
            LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(
            Long userId,
            LocalDateTime start);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(
            Long userId,
            Status status);

    Optional<Booking> findByItem_IdAndEndBeforeOrderByEndDesc(
            Long itemId,
            LocalDateTime end);

    Optional<Booking> findByItem_IdAndStartAfterOrderByEndAsc(
            Long itemId,
            LocalDateTime end);

    List<Booking> findByBooker_IdAndItem_IdAndStatusAndEndBefore(
            Long userId,
            Long itemId,
            Status status,
            LocalDateTime end);
}
