package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.Create;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ItemRequestDto {
    private long id;
    @NotBlank(groups = Create.class, message = "Item request description is blank")
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}
