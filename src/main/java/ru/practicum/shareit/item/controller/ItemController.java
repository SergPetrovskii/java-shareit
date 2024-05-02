package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
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
    public ItemDto createItem(@RequestHeader(HEADER) long userId, @Validated({Marker.OnCreate.class}) @RequestBody ItemDto itemDto) {
        Item item = itemService.createItem(userId, ItemMapper.toItem(itemDto));
        log.info("POST request for add item " + itemDto.toString() + "from user with id = " + userId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItem(@RequestHeader(HEADER) @Min(0) Long userId, @PathVariable("itemId") final Long itemId) {
        Item item = itemService.findItem(userId, itemId);
        log.info("GET request for find item with id = " + itemId + " from user with id = " + userId);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER) @Min(0) Long userId, @PathVariable("itemId") final Long itemId,
                              @Validated({Marker.OnUpdate.class}) @RequestBody ItemDto itemDto) {
        Item item = itemService.updateItem(userId, itemId, ItemMapper.toItem(itemDto));
        log.info("PATCH request for update item id = " + itemId + " from user with id = " + userId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDto> findAllItemByUser(@RequestHeader(HEADER) @Min(0) Long userId) {
        List<Item> listItem = itemService.findAllItemByUser(userId);
        log.info("GET request for find all items from user with id = " + userId);
        return ItemMapper.toListItemDto(listItem);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(HEADER) @Min(0) Long userId, @RequestParam String text) {
        List<Item> itemList = itemService.search(userId, text);
        log.info("GET request for find items");
        return ItemMapper.toListItemDto(itemList);
    }
}