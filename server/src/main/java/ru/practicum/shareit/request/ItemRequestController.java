package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createItemRequest(
            @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(USER_ID_HEADER) long owner) {
        return itemRequestService.createItemRequest(itemRequestDto, owner);
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(
            @RequestHeader(USER_ID_HEADER) long owner) {
        return itemRequestService.getUserItemRequests(owner);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequests(
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.getItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(
            @PathVariable long requestId,
            @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.getItemRequest(requestId, userId);
    }

}
