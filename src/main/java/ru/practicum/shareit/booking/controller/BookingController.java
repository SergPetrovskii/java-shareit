package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.booking.dto.BookingSearch;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.marker.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
    public BookingDto postBooking(@RequestHeader(HEADER) @Min(0) long userId,
                                  @Validated(Marker.OnCreate.class) @RequestBody @Valid BookingDtoReceived booking) {
        Booking newBooking = bookingService.postBooking(userId, BookingMapper.fromBookingDtoReceivedToBooking(booking));
        log.info("POST request for postBooking " + booking.toString() + " from user with id = " + userId);
        return BookingMapper.toBookingDto(newBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@RequestHeader(HEADER) @Min(0) final long userId,
                                      @PathVariable("bookingId") @Min(0) final long bookingId,
                                      @RequestParam boolean approved) {
        Booking newBooking = bookingService.approvedBooking(userId, bookingId, approved);
        log.info("PATCH request for approvedBooking bookingId = " + bookingId + " from user with id = " + userId);
        return BookingMapper.toBookingDto(newBooking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBooking(@RequestHeader(HEADER) @Min(0) final long userId,
                                  @PathVariable("bookingId") @Min(0) final long bookingId) {
        BookingSearch newBooking = bookingService.findBooking(userId, bookingId);
        log.info("GET request for findBooking bookingId = " + bookingId + " from user with id = " + userId);
        return BookingMapper.fromBookingSearchToBookingDto(newBooking);
    }

    @GetMapping
    public List<BookingDto> findListBooking(@RequestHeader(HEADER) @Min(0) final long userId,
                                            @RequestParam(defaultValue = "ALL") State state,
                                            @RequestParam(defaultValue = "0") @Min(0) int from,
                                            @RequestParam(defaultValue = "10") @Min(1) int size) {
        List<BookingSearch> listBooking = bookingService.findListBooking(userId, state, from, size);
        log.info("GET request for findListBooking with state = " + state + " from user with id = " + userId);
        return BookingMapper.fromBookingSearchToDtoList(listBooking);
    }

    @GetMapping("/owner")
    public List<BookingDto> findOwnerBooking(@RequestHeader(HEADER) @Min(0) final long userId,
                                             @RequestParam(defaultValue = "ALL") State state,
                                             @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @RequestParam(defaultValue = "10") @Min(1) int size) {
        List<BookingSearch> listBooking = bookingService.findListOwnerBooking(userId, state, from, size);
        log.info("GET request for findOwnerBooking with state = " + state + " from user with id = " + userId);
        return BookingMapper.fromBookingSearchToDtoList(listBooking);
    }
}
