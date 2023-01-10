package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoBookingTest {

    @Autowired
    JacksonTester<ItemDtoBooking> json;

    private ItemDtoBooking item1DtoBooking;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        User user1 = new User(1, "User1 name", "user1@mail.com");
        User user2 = new User(2, "User2 name", "user2@mail.com");

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1)
                .description("ItemRequest1 description")
                .requestor(user1)
                .created(now)
                .build();

        Item item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(itemRequest1)
                .build();
        item1DtoBooking = ItemMapper.toItemDtoBooking(item1);

        Booking booking1 = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        BookingDto booking1Dto = BookingMapper.toBookingDto(booking1);
        Booking booking2 = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        BookingDto booking2Dto = BookingMapper.toBookingDto(booking2);
        item1DtoBooking.setLastBooking(booking1Dto);
        item1DtoBooking.setNextBooking(booking2Dto);

        Comment comment1 = Comment.builder()
                .id(1)
                .text("Comment1 text")
                .item(item1)
                .author(user2)
                .created(now)
                .build();
        CommentDto comment1Dto = CommentMapper.toCommentDto(comment1);
        item1DtoBooking.setComments(List.of(comment1Dto));

    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<ItemDtoBooking> result = json.write(item1DtoBooking);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.owner");
        assertThat(result).hasJsonPath("$.request");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).extractingJsonPathNumberValue(
                "$.id").isEqualTo((int) item1DtoBooking.getId());
        assertThat(result).extractingJsonPathStringValue(
                "$.name").isEqualTo(item1DtoBooking.getName());
        assertThat(result).extractingJsonPathStringValue(
                "$.description").isEqualTo(item1DtoBooking.getDescription());
        assertThat(result).extractingJsonPathBooleanValue(
                "$.available").isEqualTo(item1DtoBooking.getAvailable());
        assertThat(result).extractingJsonPathNumberValue(
                "$.owner.id").isEqualTo((int) item1DtoBooking.getOwner().getId());
        assertThat(result).extractingJsonPathNumberValue(
                "$.request.id").isEqualTo((int) item1DtoBooking.getRequest().getId());
        assertThat(result).extractingJsonPathNumberValue(
                "$.lastBooking.id").isEqualTo((int) item1DtoBooking.getLastBooking().getId());
        assertThat(result).extractingJsonPathNumberValue(
                "$.nextBooking.id").isEqualTo((int) item1DtoBooking.getNextBooking().getId());
        assertThat(result).extractingJsonPathArrayValue(
                "$.comments").isNotEmpty();
    }

}