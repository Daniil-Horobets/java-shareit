package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Create;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ItemDtoBooking {
    private long id;
    @NotBlank(groups = Create.class, message = "Item name is blank")
    private String name;
    @NotBlank(groups = Create.class, message = "Item name is description")
    private String description;
    @NotNull(groups = Create.class, message = "Item availability is blank")
    private Boolean available;
    private User owner;
    private ItemRequest request;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}
