package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    JacksonTester<CommentDto> json;

    LocalDateTime now;
    private CommentDto comment1Dto;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();

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

        Comment comment1 = Comment.builder()
                .id(1)
                .text("Comment1 text")
                .item(item1)
                .author(user2)
                .created(now)
                .build();
        comment1Dto = CommentMapper.toCommentDto(comment1);

    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<CommentDto> result = json.write(comment1Dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathNumberValue(
                "$.id").isEqualTo((int) comment1Dto.getId());
        assertThat(result).extractingJsonPathStringValue(
                "$.text").isEqualTo(comment1Dto.getText());
        assertThat(result).extractingJsonPathStringValue(
                "$.authorName").isEqualTo(comment1Dto.getAuthorName());
    }
}