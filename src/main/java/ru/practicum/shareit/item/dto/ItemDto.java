package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.Create;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDto {
    private long id;
    @NotBlank(groups = Create.class, message = "Item name is blank")
    private String name;
    @NotBlank(groups = Create.class, message = "Item name is description")
    private String description;
    @NotNull(groups = Create.class, message = "Item availability is blank")
    private Boolean available;
    private long owner;
    private ItemRequest request;
}
