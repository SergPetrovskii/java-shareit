package ru.practicum.item.service;

import ru.practicum.item.dto.ItemSearch;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.item.model.ItemWithBookingAndComment;

import java.util.List;

public interface ItemService {
    Item createItem(Long userId, Item item);

    ItemWithBookingAndComment findItem(Long userId, Long itemId);

    Item updateItem(Long userId, Long itemId, Item itemDto);

    List<ItemWithBookingAndComment> findAllItemByUser(Long userId, int from, int size);

    List<ItemSearch> search(Long userId, String text, int from, int size);

    Comment createComment(Long userId, Long itemId, Comment comment);
}
