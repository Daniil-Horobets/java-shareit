package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.exceptions.ItemOwnerMismatchException;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ItemOwnerMismatchException("User specified as item request owner does not " +
                        "exist"));
        ItemRequest itemRequest = ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorHandler.USER_NOT_FOUND));
        return itemRequestRepository.findByRequestorIdOrderByCreated(userId).stream()
                .map(this::setItemsToItemRequest)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getItemRequests(long userId, int from, int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return itemRequestRepository.findAll(pageRequest).stream()
                .map(this::setItemsToItemRequest)
                .filter(itemRequestDto -> !(itemRequestDto.getRequestor().getId() == userId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequest(long requestId, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorHandler.USER_NOT_FOUND));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(ErrorHandler.ITEM_REQUEST_NOT_FOUND));
        return setItemsToItemRequest(itemRequest);
    }

    private ItemRequestDto setItemsToItemRequest(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<ItemDto> items = itemRepository.findByRequestId(itemRequest.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }
}
