package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.marker.Marker;

import javax.validation.constraints.Min;
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
    public ItemDto createItem(@RequestHeader(HEADER) @Min(0) long userId,
                              @Validated({Marker.OnCreate.class}) @RequestBody ItemDto itemDto) {
        Item item = itemService.createItem(userId, ItemMapper.toItem(itemDto));
        log.info("POST request for add item " + itemDto.toString() + "from user with id = " + userId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingAndComment findItem(@RequestHeader(HEADER) @Min(0) Long userId,
                                                 @PathVariable("itemId") @Min(0) final Long itemId) {
        ItemDtoWithBookingAndComment item = ItemMapper.itemDtoWithBooking(itemService.findItem(userId, itemId));
        return item;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER) @Min(0) Long userId, @PathVariable("itemId") @Min(0) final Long itemId,
                              @Validated({Marker.OnUpdate.class}) @RequestBody ItemDto itemDto) {
        Item item = itemService.updateItem(userId, itemId, ItemMapper.toItem(itemDto));
        log.info("PATCH request for update item id = " + itemId + " from user with id = " + userId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDtoWithBookingAndComment> findAllItemByUser(@RequestHeader(HEADER) @Min(0) Long userId,
                                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                                @RequestParam(defaultValue = "10") @Min(1) int size) {
        List<ItemDtoWithBookingAndComment> listItem = ItemMapper.toListItemDtoWithBooking(itemService.findAllItemByUser(userId, from, size));
        return listItem;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(HEADER) @Min(0) Long userId, @RequestParam String text,
                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                @RequestParam(defaultValue = "10") @Min(1) int size) {
        List<ItemSearch> itemList = itemService.search(userId, text, from, size);
        return ItemMapper.toListItemSearchInItemDto(itemList);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HEADER) @Min(0) Long userId,
                                    @PathVariable("itemId") @Min(0) final Long itemId,
                                    @Validated({Marker.OnCreate.class}) @RequestBody CommentDtoReceived newComment) {
        Comment comment = itemService.createComment(userId, itemId, CommentMapper.fromCommentDtoReceivedToComment(newComment));
        return CommentMapper.toCommentDto(comment);
    }
}