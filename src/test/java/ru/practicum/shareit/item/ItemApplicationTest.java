package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemSearch;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBookingAndComment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemApplicationTest {

    private final ItemService itemService;

    private final UserService userService;

    private final BookingService bookingService;

    private User user1;

    private User user2;

    private Item item;

    private Booking booking;

    @BeforeEach
    public void before() {
        user1 = userService.createUser(User.builder()
                .name("User1")
                .email("user1@mail.com")
                .build());

        user2 = userService.createUser(User.builder()
                .name("User2")
                .email("user2@mail.com")
                .build());

        item = itemService.createItem(user1.getId(), Item.builder().name("Item")
                .available(true)
                .description("Description of item")
                .build());

        booking = bookingService.postBooking(user2.getId(), Booking.builder()
                .item(item)
                .start(LocalDateTime.now())
                .finish(LocalDateTime.now().plusNanos(1))
                .build());
    }

    @AfterEach
    public void after() {
        userService.deleteUser(user1.getId());
        userService.deleteUser(user2.getId());
    }

    @Test
    public void test() {

        ItemWithBookingAndComment newItem = itemService.findItem(user1.getId(), item.getId());
        Assertions.assertNotNull(newItem);

        Item updItem = itemService.updateItem(user1.getId(), item.getId(), Item.builder().name("Item 1").build());
        Assertions.assertNotNull(updItem);

        List<ItemSearch> newList = itemService.search(user1.getId(), "Item", 1, 2);
        Assertions.assertNotNull(newList);

        bookingService.approvedBooking(user1.getId(), booking.getId(), true);

        Comment comment = itemService.createComment(user2.getId(), item.getId(), Comment.builder().text("OK").build());
        Assertions.assertNotNull(comment);
    }
}
