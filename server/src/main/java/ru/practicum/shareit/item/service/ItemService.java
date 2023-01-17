package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, long owner);

    ItemDto updateItem(long itemId, long owner, ItemDto itemDto);

    ItemDtoBooking getItem(long id, long owner);

    List<ItemDtoBooking> getUserItems(long owner, int from, int size);

    List<ItemDto> findItem(String text, int from, int size);

    CommentDto createComment(CommentDto commentDto, long itemId, long owner);
}
