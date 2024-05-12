package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingSearch;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingApplicationTest {

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
                .name("Викинг")
                .email("vikssss@mail.com")
                .build());

        user2 = userService.createUser(User.builder()
                .name("Викинг")
                .email("ssing@mail.com")
                .build());


        item = itemService.createItem(user1.getId(), Item.builder().name("оруsssжие")
                .available(true)
                .description("могучее")
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

        BookingSearch booking1 = bookingService.findBooking(user2.getId(), booking.getId());
        Assertions.assertNotNull(booking1);

        Booking booking2 = bookingService.approvedBooking(user1.getId(), booking.getId(), true);

        List<BookingSearch> list1 = bookingService.findListBooking(user2.getId(), State.ALL);
        Assertions.assertNotNull(list1);

        List<BookingSearch> list2 = bookingService.findListOwnerBooking(user1.getId(), State.ALL);
        Assertions.assertNotNull(list2);
    }
}