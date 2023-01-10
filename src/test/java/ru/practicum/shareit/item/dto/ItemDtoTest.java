package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    private ItemDto item1Dto;

    @BeforeEach
    void beforeEach() {
        User user1 = new User(1, "User1 name", "user1@mail.com");

        Item item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(null)
                .build();
        item1Dto = ItemMapper.toItemDto(item1);

    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<ItemDto> result = json.write(item1Dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.owner");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathNumberValue(
                "$.id").isEqualTo((int) item1Dto.getId());
        assertThat(result).extractingJsonPathStringValue(
                "$.name").isEqualTo(item1Dto.getName());
        assertThat(result).extractingJsonPathStringValue(
                "$.description").isEqualTo(item1Dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue(
                "$.available").isEqualTo(item1Dto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue(
                "$.owner.id").isEqualTo((int) item1Dto.getOwner().getId());
        assertThat(result).extractingJsonPathNumberValue(
                "$.requestId").isEqualTo(item1Dto.getRequestId());
    }
}