package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestDtoReceived;
import ru.practicum.request.dto.ItemRequestWithItemsDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.model.ItemRequestWithItems;
import ru.practicum.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(HEADER) long userId,
                                     @RequestBody ItemRequestDtoReceived requestDto) {
        ItemRequest request = itemRequestService.addRequest(RequestMapper.toRequest(requestDto), userId);
        log.info("POST request for add request from user with id = " + userId);
        return RequestMapper.toRequestDto(request);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> findListRequestUser(@RequestHeader(HEADER) long userId) {
        List<ItemRequestWithItems> list = itemRequestService.findListRequestUser(userId);
        log.info("GET request for find list of requests from user with id = " + userId);
        return RequestMapper.toListRequestWithItemsDto(list);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> findListRequest(@RequestHeader(HEADER) long userId,
                                                         @RequestParam(defaultValue = "0") int from,
                                                         @RequestParam(defaultValue = "10") int size) {
        log.info("GET request for find list of all requests from user with id = " + userId);
        return RequestMapper.toListRequestWithItemsDto(itemRequestService.findListRequest(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto findItemRequest(@RequestHeader(HEADER) long userId,
                                                   @PathVariable("requestId") long requestId) {
        log.info("GET request for find Item request with Id = " + requestId + " from user with id = " + userId);
        return RequestMapper.toRequestWithItemsDto(itemRequestService.findItemRequest(userId, requestId));
    }
}