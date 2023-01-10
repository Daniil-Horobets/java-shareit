package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    JacksonTester<ItemRequestDto> json;

    private ItemRequestDto itemRequest1Dto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = new User(1, "User1 name", "user1@mail.com");

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1)
                .description("ItemRequest1 description")
                .requestor(user1)
                .created(now)
                .build();
        itemRequest1Dto = ItemRequestMapper.toItemRequestDto(itemRequest1);

        Item item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(itemRequest1)
                .build();
        ItemDto item1Dto = ItemMapper.toItemDto(item1);

        itemRequest1Dto.setItems(List.of(item1Dto));

    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<ItemRequestDto> result = json.write(itemRequest1Dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.requestor");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue(
                "$.id").isEqualTo((int) itemRequest1Dto.getId());
        assertThat(result).extractingJsonPathStringValue(
                "$.description").isEqualTo(itemRequest1Dto.getDescription());
        assertThat(result).extractingJsonPathNumberValue(
                "$.requestor.id").isEqualTo((int) itemRequest1Dto.getRequestor().getId());
        assertThat(result).extractingJsonPathArrayValue(
                "$.items").isNotEmpty();
    }
}