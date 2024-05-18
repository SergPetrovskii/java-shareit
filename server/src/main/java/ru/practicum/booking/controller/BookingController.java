package ru.practicum.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoReceived;
import ru.practicum.booking.dto.BookingSearch;
import ru.practicum.booking.mapper.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.State;
import ru.practicum.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto postBooking(@RequestHeader(HEADER) long userId, @RequestBody BookingDtoReceived booking) {
        Booking newBooking = bookingService.postBooking(userId, BookingMapper.fromBookingDtoReceivedToBooking(booking));
        log.info("POST request for postBooking " + booking.toString() + " from user with id = " + userId);
        return BookingMapper.toBookingDto(newBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@RequestHeader(HEADER) final long userId,
                                      @PathVariable("bookingId") final long bookingId,
                                      @RequestParam boolean approved) {
        Booking newBooking = bookingService.approvedBooking(userId, bookingId, approved);
        log.info("PATCH request for approvedBooking bookingId = " + bookingId + " from user with id = " + userId);
        return BookingMapper.toBookingDto(newBooking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBooking(@RequestHeader(HEADER) final long userId,
                                  @PathVariable("bookingId") final long bookingId) {
        BookingSearch newBooking = bookingService.findBooking(userId, bookingId);
        log.info("GET request for findBooking bookingId = " + bookingId + " from user with id = " + userId);
        return BookingMapper.fromBookingSearchToBookingDto(newBooking);
    }

    @SneakyThrows
    @GetMapping
    public List<BookingDto> findListBooking(@RequestHeader(HEADER) final long userId,
                                            @RequestParam(defaultValue = "ALL") String stateParam,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        List<BookingSearch> listBooking = bookingService.findListBooking(userId, state, from, size);
        log.info("GET request for findListBooking with state = " + state + " from user with id = " + userId);
        return BookingMapper.fromBookingSearchToDtoList(listBooking);
    }

    @SneakyThrows
    @GetMapping("/owner")
    public List<BookingDto> findOwnerBooking(@RequestHeader(HEADER) final long userId,
                                             @RequestParam(defaultValue = "ALL") String stateParam,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        List<BookingSearch> listBooking = bookingService.findListOwnerBooking(userId, state, from, size);
        log.info("GET request for findOwnerBooking with state = " + state + " from user with id = " + userId);
        return BookingMapper.fromBookingSearchToDtoList(listBooking);
    }
}
