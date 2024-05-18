package ru.practicum.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dao.BookingRepository;
import ru.practicum.booking.dto.BookingSearch;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.State;
import ru.practicum.booking.model.Status;
import ru.practicum.exception.AvailableException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.UnsupportedStatusException;
import ru.practicum.item.dao.ItemRepository;
import ru.practicum.item.model.Item;
import ru.practicum.user.dao.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public Booking postBooking(Long userId, Booking booking) {
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if (item.getAvailable().equals(false)) {
            throw new AvailableException("Item is not available for booking");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Item is not available for your booking");
        }
        booking.setItem(item);
        booking.setBooker(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found")));
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Transactional
    @Override
    public Booking approvedBooking(Long userId, Long bookingId, Boolean answer) {
        boolean isBookingExist = bookingRepository.existsByItemOwnerIdOrBookerId(userId, userId);
        if (!isBookingExist) {
            throw new AvailableException("Booking is not available");
        }
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Booking is not found"));
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new AvailableException("Item can't be approved");
        }
        if (answer) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return booking;
    }

    @Override
    public BookingSearch findBooking(long userId, long bookingId) {
        return bookingRepository.findBooking(bookingId, userId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Booking is not found"));
    }

    @Override
    public List<BookingSearch> findListBooking(long userId, State state, int from, int size) throws UnsupportedStatusException {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("start").descending());
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStateCurrent(userId, pageable);
            case PAST:
                return bookingRepository.findAllByBookerIdAndStatePast(userId, Status.APPROVED, pageable);
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStateFuture(userId, pageable);
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable);
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable);
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingSearch> findListOwnerBooking(long userId, State state, int from, int size) throws UnsupportedStatusException {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("start").descending());
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllByItemOwnerAndStateCurrent(userId, pageable);
            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndStatePast(userId, Status.APPROVED, pageable);
            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStateFuture(userId, Status.REJECTED, pageable);
            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable);
            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable);
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageable);
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
