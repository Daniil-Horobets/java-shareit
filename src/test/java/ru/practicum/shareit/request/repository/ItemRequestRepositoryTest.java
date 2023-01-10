package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;


    LocalDateTime now;
    private User user1;
    private ItemRequest itemRequest1;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        user1 = new User(1, "User1 name", "user1@mail.com");
        user1 = userRepository.save(user1);

        itemRequest1 = ItemRequest.builder()
                .id(1)
                .description("ItemRequest1 description")
                .requestor(user1)
                .created(now)
                .build();
        itemRequest1 = itemRequestRepository.save(itemRequest1);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findByRequestorIdOrderByCreatedTest() {
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreated(user1.getId());

        assertEquals(List.of(itemRequest1), itemRequests);
    }
}