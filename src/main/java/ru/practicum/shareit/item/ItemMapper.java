package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();
    }

    public static ItemDtoBooking toItemDtoBooking(Item item) {
        return ItemDtoBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();
    }

    public static Item toItemFromItemDtoBooking(ItemDtoBooking itemDtoBooking) {
        return Item.builder()
                .id(itemDtoBooking.getId())
                .name(itemDtoBooking.getName())
                .description(itemDtoBooking.getDescription())
                .available(itemDtoBooking.getAvailable())
                .owner(itemDtoBooking.getOwner())
                .request(itemDtoBooking.getRequest())
                .build();
    }
}
