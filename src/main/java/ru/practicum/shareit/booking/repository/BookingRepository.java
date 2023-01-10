package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
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
            Long userId,
            PageRequest pageRequest);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end,
            PageRequest pageRequest);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(
            Long userId,
            LocalDateTime end,
            PageRequest pageRequest);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(
            Long userId,
            LocalDateTime start,
            PageRequest pageRequest);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(
            Long userId,
            Status status,
            PageRequest pageRequest);

    List<Booking> findByItemOwnerIdOrderByStartDesc(
            Long userId,
            PageRequest pageRequest);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end,
            PageRequest pageRequest);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(
            Long userId,
            LocalDateTime end,
            PageRequest pageRequest);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(
            Long userId,
            LocalDateTime start,
            PageRequest pageRequest);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(
            Long userId,
            Status status,
            PageRequest pageRequest);

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
