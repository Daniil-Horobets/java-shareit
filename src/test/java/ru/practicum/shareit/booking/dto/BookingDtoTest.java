package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    JacksonTester<BookingDto> json;

    LocalDateTime now;
    LocalDateTime start;
    LocalDateTime end;
    private BookingDto booking1Dto;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        start = now.plusDays(1);
        end = now.plusDays(2);

        User user1 = new User(1, "User1 name", "user1@mail.com");
        User user2 = new User(2, "User2 name", "user2@mail.com");

        Item item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(null)
                .build();

        Booking booking1 = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        booking1Dto = BookingMapper.toBookingDto(booking1);
    }


    @Test
    void testSerialize() throws Exception {
        JsonContent<BookingDto> result = json.write(booking1Dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue(
                "$.id").isEqualTo((int) booking1Dto.getId());
        assertThat(result).extractingJsonPathNumberValue(
                "$.itemId").isEqualTo((int) booking1Dto.getItemId());
        assertThat(result).extractingJsonPathNumberValue(
                "$.bookerId").isEqualTo((int) booking1Dto.getBookerId());
        assertThat(result).extractingJsonPathStringValue(
                "$.status").isEqualTo(booking1Dto.getStatus().toString());
    }

}