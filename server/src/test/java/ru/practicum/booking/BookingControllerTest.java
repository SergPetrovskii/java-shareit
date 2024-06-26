package ru.practicum.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.booking.controller.BookingController;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoReceived;
import ru.practicum.booking.dto.BookingSearch;
import ru.practicum.booking.mapper.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.State;
import ru.practicum.booking.model.Status;
import ru.practicum.booking.service.BookingService;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void postBooking() {
        long userId = 1L;
        long itemId = 1L;
        Item item = Item.builder().id(itemId).build();
        User user = User.builder().id(userId).build();

        Booking booking = Booking.builder().item(item).build();
        BookingDtoReceived bookingDtoReceived = BookingDtoReceived.builder().itemId(itemId).build();
        Booking booking1 = BookingMapper.fromBookingDtoReceivedToBooking(bookingDtoReceived);
        booking1.setBooker(user);

        when(bookingService.postBooking(userId, booking)).thenReturn(booking1);

        BookingDto newBooking = bookingController.postBooking(userId, bookingDtoReceived);
        BookingDto newBookingDto = BookingMapper.toBookingDto(booking1);

        assertEquals(newBooking, newBookingDto);
    }

    @Test
    void approvedBooking() {
        long userId = 1L;
        long itemId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        User user = User.builder().id(userId).build();
        Item item = Item.builder().id(itemId).owner(user).available(true).build();
        Booking booking = Booking.builder().item(item).booker(user).status(Status.APPROVED).build();

        when(bookingService.approvedBooking(userId, bookingId, approved)).thenReturn(booking);

        BookingDto bookingDto = bookingController.approvedBooking(userId, bookingId, approved);
        BookingDto bookingDto1 = BookingMapper.toBookingDto(booking);

        assertEquals(bookingDto, bookingDto1);
    }

    @Test
    void findBooking() {
        long userId = 1L;
        long itemId = 1L;
        long bookingId = 1L;
        Item item = Item.builder().id(itemId).build();
        User user = User.builder().id(userId).build();
        BookingSearch booking = BookingSearch.builder().item(item).booker(user).build();

        when(bookingService.findBooking(userId, bookingId)).thenReturn(booking);

        BookingDto booking1 = bookingController.findBooking(userId, bookingId);
        BookingDto bookingDto = BookingMapper.fromBookingSearchToBookingDto(booking);

        assertEquals(booking1, bookingDto);
    }

    @SneakyThrows
    @Test
    void findListBooking() {
        long userId = 1L;

        when(bookingService.findListBooking(userId, State.ALL, 0, 10)).thenReturn(List.of());

        List<BookingDto> listBooking = bookingController.findListBooking(userId, State.ALL, 0, 10);
        List<BookingDto> listBooking1 = List.of();

        assertEquals(listBooking, listBooking1);
    }

    @SneakyThrows
    @Test
    void findOwnerBooking() {
        long userId = 1L;

        when(bookingService.findListOwnerBooking(userId, State.ALL, 0, 10)).thenReturn(List.of());

        List<BookingDto> listBooking = bookingController.findOwnerBooking(userId, State.ALL, 0, 10);
        List<BookingDto> listBooking1 = List.of();

        assertEquals(listBooking, listBooking1);
    }
}
