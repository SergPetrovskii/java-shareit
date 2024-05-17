package ru.practicum.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.*;
import ru.practicum.item.mapper.CommentMapper;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.item.service.ItemService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto) {
        Item item = itemService.createItem(userId, ItemMapper.toItem(itemDto));
        log.info("POST request for add item " + itemDto.toString() + "from user with id = " + userId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingAndComment findItem(@RequestHeader(HEADER) Long userId,
                                                 @PathVariable("itemId") final Long itemId) {
        ItemDtoWithBookingAndComment item = ItemMapper.itemDtoWithBooking(itemService.findItem(userId, itemId));
        return item;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER) Long userId, @PathVariable("itemId") final Long itemId,
                              @RequestBody ItemDto itemDto) {
        Item item = itemService.updateItem(userId, itemId, ItemMapper.toItem(itemDto));
        log.info("PATCH request for update item id = " + itemId + " from user with id = " + userId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDtoWithBookingAndComment> findAllItemByUser(@RequestHeader(HEADER) Long userId,
                                                                @RequestParam(defaultValue = "0") int from,
                                                                @RequestParam(defaultValue = "10") int size) {
        List<ItemDtoWithBookingAndComment> listItem = ItemMapper.toListItemDtoWithBooking(itemService.findAllItemByUser(userId, from, size));
        return listItem;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(HEADER) Long userId, @RequestParam String text,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        List<ItemSearch> itemList = itemService.search(userId, text, from, size);
        return ItemMapper.toListItemSearchInItemDto(itemList);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HEADER) Long userId,
                                    @PathVariable("itemId") final Long itemId, @RequestBody CommentDtoReceived newComment) {
        Comment comment = itemService.createComment(userId, itemId, CommentMapper.fromCommentDtoReceivedToComment(newComment));
        return CommentMapper.toCommentDto(comment);
    }
}