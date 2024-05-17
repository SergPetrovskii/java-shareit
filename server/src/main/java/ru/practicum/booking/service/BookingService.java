package ru.practicum.booking.service;

import ru.practicum.booking.dto.BookingSearch;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.State;

import java.util.List;

public interface BookingService {

    Booking postBooking(Long userId, Booking booking);

    Booking approvedBooking(Long userId, Long bookingId, Boolean answer);

    BookingSearch findBooking(long userId, long bookingId);

    List<BookingSearch> findListBooking(long userId, State state, int from, int size);

    List<BookingSearch> findListOwnerBooking(long userId, State state, int from, int size);
}
