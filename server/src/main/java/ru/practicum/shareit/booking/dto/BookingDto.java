package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;
    private long bookerId;
    private Status status;
}
