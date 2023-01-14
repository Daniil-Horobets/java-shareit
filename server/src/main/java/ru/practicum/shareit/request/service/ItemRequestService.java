package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getUserItemRequests(long userId);

    List<ItemRequestDto> getItemRequests(long userId, int from, int size);

    ItemRequestDto getItemRequest(long requestId, long userId);
}
