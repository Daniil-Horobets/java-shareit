package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long owner) {
        User user = userRepository.findById(owner)
                .orElseThrow(() -> new ItemOwnerMismatchException("User specified as item owner does not exist"));
        Item item = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .request(null)
                .build();
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(long itemId, long owner, ItemDto itemDto) {

        ItemDto initialItem = ItemMapper.toItemDto(ItemMapper.toItemFromItemDtoBooking(getItem(itemId, owner)));
        if (initialItem.getOwner().getId() != owner) {
            throw new ItemOwnerMismatchException(ErrorHandler.ITEM_OWNER_MISMATCH);
        }
        if (itemDto.getName() != null) {
            initialItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            initialItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            initialItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getOwner() != null) {
            initialItem.setOwner(itemDto.getOwner());
        }
        if (itemDto.getRequest() != null) {
            initialItem.setRequest(itemDto.getRequest());
        }
        itemRepository.save(ItemMapper.toItem(initialItem));
        return initialItem;
    }

    @Override
    public ItemDtoBooking getItem(long id, long owner) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(ErrorHandler.ITEM_NOT_FOUND));
        return setBookingsAndCommentsToItem(owner, item);
    }

    @Override
    public List<ItemDtoBooking> getUserItems(long owner) {
        return itemRepository.findByOwnerIdOrderById(owner).stream()
                .map(item -> setBookingsAndCommentsToItem(owner, item))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItem(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, long itemId, long userId) {
        LocalDateTime now = LocalDateTime.now();
        boolean isBookingsOfItemByUserEmpty = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                userId,
                itemId,
                Status.APPROVED,
                now
        ).isEmpty();
        if (isBookingsOfItemByUserEmpty) {
            throw new BookingStatusMismatchException("User did not booked item or booking was rejected or " +
                    "booking not yet finished");
        }
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(itemRepository.findById(itemId)
                        .orElseThrow(() -> new ItemNotFoundException(ErrorHandler.ITEM_NOT_FOUND)))
                .author(userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(ErrorHandler.USER_NOT_FOUND)))
                .created(now)
                .build();
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private ItemDtoBooking setBookingsAndCommentsToItem(long owner, Item item) {
        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);
        if (item.getOwner().getId() == owner) {
            LocalDateTime now = LocalDateTime.now();
            itemDtoBooking.setLastBooking(
                    bookingRepository.findByItemIdAndEndBeforeOrderByEndDesc(
                            itemDtoBooking.getId(),
                            now
                    ).map(BookingMapper::toBookingDto).orElse(null));
            itemDtoBooking.setNextBooking(
                    bookingRepository.findByItemIdAndStartAfterOrderByEndAsc(
                            itemDtoBooking.getId(),
                            now
                    ).map(BookingMapper::toBookingDto).orElse(null));
        } else {
            itemDtoBooking.setLastBooking(null);
            itemDtoBooking.setNextBooking(null);
        }
        List<Comment> comments = commentRepository.findByItemId(itemDtoBooking.getId());
        itemDtoBooking.setComments(comments == null || comments.isEmpty() ? Collections.emptyList() :
                comments.stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()));
        return itemDtoBooking;
    }
}
