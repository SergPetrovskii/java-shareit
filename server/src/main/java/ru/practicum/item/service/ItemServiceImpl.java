package ru.practicum.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dao.BookingRepository;
import ru.practicum.booking.dto.BookingSearch;
import ru.practicum.booking.mapper.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.SmallBooking;
import ru.practicum.booking.model.Status;
import ru.practicum.exception.CommentException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.item.dao.CommentRepository;
import ru.practicum.item.dao.ItemRepository;
import ru.practicum.item.dto.ItemSearch;
import ru.practicum.item.mapper.CommentMapper;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.CommentReceiving;
import ru.practicum.item.model.Item;
import ru.practicum.item.model.ItemWithBookingAndComment;
import ru.practicum.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public Item createItem(Long userId, Item item) {
        item.setOwner(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found")));
        return itemRepository.save(item);
    }

    @Override
    public ItemWithBookingAndComment findItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Item not found"));
        ItemWithBookingAndComment itemWithBooking = ItemMapper.itemWithBooking(item);

        List<CommentReceiving> listComment = CommentMapper.toListCommentReceiving(commentRepository.findAllByItemId(itemId));

        List<Booking> bokklist = bookingRepository.findAllByItemIdAndItemOwnerIdAndStatusOrderByStart(itemId, userId, Status.APPROVED);

        final LocalDateTime timeNow = LocalDateTime.now();

        SmallBooking lastBooking = bokklist.stream()
                .filter(a -> !a.getFinish().isAfter(timeNow))
                .map(BookingMapper::toSmallBooking)
                .reduce((a, b) -> b)
                .orElse(null);

        SmallBooking nextBooking = bokklist.stream()
                .filter(a -> a.getFinish().isAfter(timeNow))
                .map(BookingMapper::toSmallBooking)
                .findFirst()
                .orElse(null);

        itemWithBooking.addBooking(lastBooking, nextBooking);
        itemWithBooking.addComments(listComment);
        return itemWithBooking;
    }

    @Transactional
    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        Item newItem = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();
        if (name != null && !name.isBlank()) {
            newItem.setName(name);
        }
        if (description != null && !description.isBlank()) {
            newItem.setDescription(description);
        }
        if (available != null) {
            newItem.setAvailable(available);
        }
        return newItem;
    }

    @Override
    public List<ItemWithBookingAndComment> findAllItemByUser(Long userId, int from, int size) {

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        List<ItemWithBookingAndComment> result = itemRepository.findAllByOwnerId(userId, pageable)
                .stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(a -> ItemMapper.itemWithBooking(a))
                .collect(Collectors.toList());

        Map<Long, List<CommentReceiving>> listComment = commentRepository.findAllByUserId(userId)
                .stream()
                .map(a -> CommentMapper.fromCommentToCommentReceiving(a))
                .collect(Collectors.groupingBy(c -> c.getItem(), Collectors.toList()));

        Map<Long, List<Booking>> listBooking = bookingRepository.findAllByItemOwnerIdOrderByStart(userId)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId(), Collectors.toList()));

        final LocalDateTime timeNow = LocalDateTime.now();

        result.stream()
                .forEach(item -> {
                    SmallBooking lastBooking = listBooking.getOrDefault(item.getId(), List.of())
                            .stream()
                            .filter(a -> !a.getFinish().isAfter(timeNow))
                            .reduce((a, b) -> b)
                            .map(BookingMapper::toSmallBooking)
                            .orElse(null);

                    SmallBooking nextBooking = listBooking.getOrDefault(item.getId(), List.of())
                            .stream()
                            .filter(a -> a.getFinish().isAfter(timeNow))
                            .findFirst()
                            .map(BookingMapper::toSmallBooking)
                            .orElse(null);

                    item.addBooking(lastBooking, nextBooking);
                });

        result.stream()
                .forEach(item -> {
                    List<CommentReceiving> list =
                            listComment.getOrDefault(item.getId(), List.of());
                    item.addComments(list);
                });

        return result;
    }

    @Override
    public List<ItemSearch> search(Long userId, String text, int from, int size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findItemSearch(text, text, pageable);
    }

    @Transactional
    @Override
    public Comment createComment(Long userId, Long itemId, Comment newComment) {
        final LocalDateTime timeNow = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 1);
        List<BookingSearch> bookingList = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndFinishBefore(itemId, userId,
                Status.APPROVED, pageable);
        if (bookingList.isEmpty()) {
            throw new CommentException("The User is not booked this item");
        }
        BookingSearch booking = bookingList.get(0);

        Comment comment = Comment.builder()
                .item(booking.getItem())
                .create(timeNow)
                .user(booking.getBooker())
                .text(newComment.getText())
                .build();

        return commentRepository.save(comment);
    }
}