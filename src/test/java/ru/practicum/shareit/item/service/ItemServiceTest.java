package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingStatusMismatchException;
import ru.practicum.shareit.exceptions.ItemOwnerMismatchException;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    LocalDateTime now;
    LocalDateTime start;
    LocalDateTime end;
    private User user1;
    private User user2;
    private Item item1;
    private Booking booking1;
    private Comment comment1;


    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        start = now.plusDays(1);
        end = now.plusDays(2);

        user1 = new User(1, "User1 name", "user1@mail.com");
        user2 = new User(2, "User2 name", "user2@mail.com");

        item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(null)
                .build();

        booking1 = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(Status.WAITING)
                .build();

        comment1 = Comment.builder()
                .id(1)
                .text("Comment1 text")
                .item(item1)
                .author(user2)
                .created(now)
                .build();
    }

    @Test
    void createItemTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        ItemDto itemDto = itemService.createItem(
                ItemMapper.toItemDto(item1),
                user1.getId());

        assertEquals(1, itemDto.getId());
        assertEquals("Item1 name", itemDto.getName());
        assertEquals("Item1 description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertEquals(user1, itemDto.getOwner());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void createItemWithWrongOwnerTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ItemOwnerMismatchException exception = assertThrows(ItemOwnerMismatchException.class,
                () -> itemService.createItem(
                        ItemMapper.toItemDto(item1),
                        user1.getId()));

        assertEquals("User specified as item owner does not exist", exception.getMessage());
    }

    @Test
    void createItemWithWrongRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ItemDto itemDto = ItemMapper.toItemDto(item1);
        itemDto.setRequestId(3L);
        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
                () -> itemService.createItem(
                        itemDto,
                        user1.getId()));

        assertEquals("Item request not found", exception.getMessage());
    }

    @Test
    void updateItemTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        ItemDto itemDto = itemService.updateItem(
                item1.getId(),
                user1.getId(),
                ItemMapper.toItemDto(item1));

        assertEquals(1, itemDto.getId());
        assertEquals("Item1 name", itemDto.getName());
        assertEquals("Item1 description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertEquals(user1, itemDto.getOwner());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void updateItemFromNotOwnerTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        ItemOwnerMismatchException exception = assertThrows(ItemOwnerMismatchException.class,
                () -> itemService.updateItem(
                        item1.getId(),
                        user2.getId(),
                        ItemMapper.toItemDto(item1)));

        assertEquals("User not an item owner", exception.getMessage());
    }

    @Test
    void getItemTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        ItemDtoBooking itemDtoBooking = itemService.getItem(
                item1.getId(),
                user1.getId());

        assertEquals(1, itemDtoBooking.getId());
        assertEquals("Item1 name", itemDtoBooking.getName());
        assertEquals("Item1 description", itemDtoBooking.getDescription());
        assertEquals(true, itemDtoBooking.getAvailable());
        assertEquals(user1, itemDtoBooking.getOwner());
        assertNull(itemDtoBooking.getRequest());
    }

    @Test
    void getUserItemsTest() {
        when(itemRepository.findByOwnerIdOrderById(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(item1));

        List<ItemDtoBooking> itemDtoBookings = itemService.getUserItems(
                user1.getId(),
                0,
                20);

        assertEquals(1, itemDtoBookings.size());
        assertEquals(1, itemDtoBookings.get(0).getId());
        assertEquals("Item1 name", itemDtoBookings.get(0).getName());
        assertEquals("Item1 description", itemDtoBookings.get(0).getDescription());
        assertEquals(true, itemDtoBookings.get(0).getAvailable());
        assertEquals(user1, itemDtoBookings.get(0).getOwner());
        assertNull(itemDtoBookings.get(0).getRequest());
    }

    @Test
    void findItemTest() {
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                anyString(),
                anyString(),
                any(PageRequest.class)))
                .thenReturn(List.of(item1));

        List<ItemDto> itemDtos = itemService.findItem(
                "Item1",
                0,
                20);

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("Item1 name", itemDtos.get(0).getName());
        assertEquals("Item1 description", itemDtos.get(0).getDescription());
        assertEquals(true, itemDtos.get(0).getAvailable());
        assertEquals(user1, itemDtos.get(0).getOwner());
        assertNull(itemDtos.get(0).getRequestId());
    }

    @Test
    void findItemByEmptyTextTest() {
        List<ItemDto> itemDtos = itemService.findItem(
                "",
                0,
                20);

        assertEquals(Collections.emptyList(), itemDtos);
    }

    @Test
    void createCommentTest() {
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                anyLong(),
                anyLong(),
                any(Status.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(booking1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment1);

        CommentDto commentDto = itemService.createComment(
                CommentMapper.toCommentDto(comment1),
                1,
                1);

        assertEquals(1, commentDto.getId());
        assertEquals("Comment1 text", commentDto.getText());
        assertEquals("User2 name", commentDto.getAuthorName());
        assertEquals(now, commentDto.getCreated());
    }

    @Test
    void createCommentFromUserWithoutBookingTest() {
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                anyLong(),
                anyLong(),
                any(Status.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        BookingStatusMismatchException exception = assertThrows(BookingStatusMismatchException.class,
                () -> itemService.createComment(
                        CommentMapper.toCommentDto(comment1),
                        1,
                        1));

        assertEquals(
                "User did not booked item or booking was rejected or booking not yet finished",
                exception.getMessage());
    }
}