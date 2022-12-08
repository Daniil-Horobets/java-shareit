package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, long owner);

    ItemDto updateItem(long itemId, long owner, ItemDto itemDto);

    ItemDto getItem(long id);

    List<ItemDto> getUserItems(long owner);

    List<ItemDto> findItem(String text);
}
