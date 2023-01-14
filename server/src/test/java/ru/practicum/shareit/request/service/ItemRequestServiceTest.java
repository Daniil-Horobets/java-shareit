package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.ItemOwnerMismatchException;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemRequestServiceTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private LocalDateTime now;
    private User user1;
    private User user2;
    private ItemRequest itemRequest1;


    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();

        user1 = new User(1, "User1 name", "user1@mail.com");
        user2 = new User(2, "User2 name", "user2@mail.com");

        itemRequest1 = ItemRequest.builder()
                .id(1)
                .description("ItemRequest1 description")
                .requestor(user1)
                .created(now)
                .build();
    }

    @Test
    void createItemRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest1);

        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(
                ItemRequestMapper.toItemRequestDto(itemRequest1),
                user1.getId());

        assertEquals(1, itemRequestDto.getId());
        assertEquals("ItemRequest1 description", itemRequestDto.getDescription());
        assertEquals(user1, itemRequestDto.getRequestor());
        assertEquals(now, itemRequestDto.getCreated());
        assertEquals(Collections.emptyList(), itemRequestDto.getItems());
    }

    @Test
    void createItemRequestFromWrongUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ItemOwnerMismatchException exception = assertThrows(ItemOwnerMismatchException.class,
                () -> itemRequestService.createItemRequest(
                        ItemRequestMapper.toItemRequestDto(itemRequest1),
                        user1.getId()));

        assertEquals(
                "User specified as item request owner does not exist",
                exception.getMessage());
    }

    @Test
    void getUserItemRequestsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findByRequestorIdOrderByCreated(anyLong()))
                .thenReturn(List.of(itemRequest1));

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getUserItemRequests(
                user1.getId());

        assertEquals(1, itemRequestDtos.size());
        assertEquals(1, itemRequestDtos.get(0).getId());
        assertEquals("ItemRequest1 description", itemRequestDtos.get(0).getDescription());
        assertEquals(user1, itemRequestDtos.get(0).getRequestor());
        assertEquals(now, itemRequestDtos.get(0).getCreated());
        assertEquals(Collections.emptyList(), itemRequestDtos.get(0).getItems());
    }

    @Test
    void getUserItemRequestsWrongUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getUserItemRequests(
                        user1.getId()));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getItemRequestsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRequestRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest1)));

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getItemRequests(
                user2.getId(),
                0,
                10);

        assertEquals(1, itemRequestDtos.size());
        assertEquals(1, itemRequestDtos.get(0).getId());
        assertEquals("ItemRequest1 description", itemRequestDtos.get(0).getDescription());
        assertEquals(user1, itemRequestDtos.get(0).getRequestor());
        assertEquals(now, itemRequestDtos.get(0).getCreated());
        assertEquals(Collections.emptyList(), itemRequestDtos.get(0).getItems());
    }

    @Test
    void getItemRequestsFromRequestOwnerTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest1)));

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getItemRequests(
                user1.getId(),
                0,
                10);

        assertEquals(Collections.emptyList(), itemRequestDtos);
    }

    @Test
    void getItemRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest1));

        ItemRequestDto itemRequestDto = itemRequestService.getItemRequest(
                itemRequest1.getId(),
                user1.getId());

        assertEquals(1, itemRequestDto.getId());
        assertEquals("ItemRequest1 description", itemRequestDto.getDescription());
        assertEquals(user1, itemRequestDto.getRequestor());
        assertEquals(now, itemRequestDto.getCreated());
        assertEquals(Collections.emptyList(), itemRequestDto.getItems());
    }

    @Test
    void getItemRequestWrongUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequest(
                        itemRequest1.getId(),
                        user1.getId()));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getItemRequestWrongRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequest(
                        itemRequest1.getId(),
                        user1.getId()));

        assertEquals("Item request not found", exception.getMessage());
    }
}