package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;
    private ItemRequest itemRequest1;
    private Item item1;

    @BeforeEach
    void beforeEach() {

        LocalDateTime now = LocalDateTime.now();

        user1 = new User(1, "User1 name", "user1@mail.com");
        User user2 = new User(2, "User2 name", "user2@mail.com");
        user1 = userRepository.save(user1);
        userRepository.save(user2);

        itemRequest1 = ItemRequest.builder()
                .id(1)
                .description("ItemRequest1 description")
                .requestor(user1)
                .created(now)
                .build();
        itemRequest1 = itemRequestRepository.save(itemRequest1);

        item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(itemRequest1)
                .build();
        item1 = itemRepository.save(item1);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findByOwnerIdOrderByIdTest() {
        List<Item> items = itemRepository.findByOwnerIdOrderById(user1.getId(), PageRequest.of(0, 10));

        assertEquals(List.of(item1), items);
    }

    @Test
    void findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrueTest() {
        String text = "Item1";
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                text, text, PageRequest.of(0, 10));

        assertEquals(List.of(item1), items);
    }

    @Test
    void findByRequestIdTest() {
        List<Item> items = itemRepository.findByRequestId(itemRequest1.getId());

        assertEquals(List.of(item1), items);
    }
}