package ru.practicum.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.item.dao.ItemRepository;
import ru.practicum.item.dto.ItemSearch;
import ru.practicum.item.model.Item;
import ru.practicum.request.dao.ItemRequestRepository;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotEmpty;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;


    private long userId = 1L;
    private long itemId = 1L;
    private long itemRequestId = 1L;

    private Pageable pageable = PageRequest.of(0, 10);
    private User user = User.builder().id(userId).name("user").email("user@mail.com").build();
    private Item item = Item.builder()
            .id(itemId).name("Item1").available(true).description("Item descr").requestId(itemRequestId).owner(user).build();

    private ItemRequest itemRequest = ItemRequest.builder()
            .description("qwe").id(itemRequestId).userId(userId).created(LocalDateTime.now().withNano(0)).build();

    @BeforeEach
    void before() {
        user = userRepository.save(user);
        userId = user.getId();

        itemRequest.setUserId(userId);
        itemRequest = itemRequestRepository.save(itemRequest);
        itemRequestId = itemRequest.getId();

        item.setOwner(user);
        item.setRequestId(itemRequestId);
        item = itemRepository.save(item);
        itemId = item.getId();
    }

    @AfterEach
    void after() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findAllByOwnerId() {

        List<Item> itemList = itemRepository.findAllByOwnerId(userId, pageable);

        assertNotNull(itemList);
    }

    @Test
    void findItemSearch() {

        String text = "Item1";

        List<ItemSearch> itemSearch = itemRepository.findItemSearch(text, text, pageable);

        assertEquals(1, itemSearch.size());
    }

    @Test
    void findByIdAndOwnerId() {

        Optional<Item> byIdAndOwnerId = itemRepository.findByIdAndOwnerId(itemId, userId);

        assertTrue(byIdAndOwnerId.isPresent());
    }

    @Test
    void findAllByRequestIds() {
        List<Long> list = List.of(itemRequestId);

        List<Item> allByRequestId = itemRepository.findAllByRequestIds(list);

        assertNotEmpty(allByRequestId, "list is not empty");
    }

    @Test
    void findAllByRequestId() {
        List<Item> allByRequestId = itemRepository.findAllByRequestId(itemRequestId);

        assertNotEmpty(allByRequestId, "list is not empty");
    }
}
