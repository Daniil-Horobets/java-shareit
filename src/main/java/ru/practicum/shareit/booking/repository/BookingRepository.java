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
    List<Booking> findByBookerIdOrderByStartDesc(
            Long userId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(
            Long userId,
            LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(
            Long userId,
            LocalDateTime start);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(
            Long userId,
            Status status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(
            Long userId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(
            Long userId,
            LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(
            Long userId,
            LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(
            Long userId,
            Status status);

    Optional<Booking> findByItemIdAndEndBeforeOrderByEndDesc(
            Long itemId,
            LocalDateTime end);

    Optional<Booking> findByItemIdAndStartAfterOrderByEndAsc(
            Long itemId,
            LocalDateTime end);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(
            Long userId,
            Long itemId,
            Status status,
            LocalDateTime end);
}
