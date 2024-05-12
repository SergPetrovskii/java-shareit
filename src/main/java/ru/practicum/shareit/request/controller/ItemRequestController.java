package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoReceived;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
    public ItemRequestDto addRequest(@RequestHeader(HEADER) @Min(0) long userId,
                                     @RequestBody @Valid ItemRequestDtoReceived requestDto) {
        ItemRequest request = itemRequestService.addRequest(RequestMapper.toRequest(requestDto), userId);
        log.info("POST request for add request from user with id = " + userId);
        return RequestMapper.toRequestDto(request);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> findListRequestUser(@RequestHeader(HEADER) @Min(0) long userId) {
        List<ItemRequestWithItems> list = itemRequestService.findListRequestUser(userId);
        log.info("GET request for find list of requests from user with id = " + userId);
        return RequestMapper.toListRequestWithItemsDto(list);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> findListRequest(@RequestHeader(HEADER) @Min(0) long userId,
                                                         @Min(0) @RequestParam(defaultValue = "0")  int from,
                                                         @Min(1) @RequestParam(defaultValue = "10")  int size) {
        log.info("GET request for find list of all requests from user with id = " + userId);
        return RequestMapper.toListRequestWithItemsDto(itemRequestService.findListRequest(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto findItemRequest(@RequestHeader(HEADER) @Min(0) long userId,
                                                   @PathVariable("requestId") @Min(0) long requestId) {
        log.info("GET request for find Item request with Id = " + requestId + " from user with id = " + userId);
        return RequestMapper.toRequestWithItemsDto(itemRequestService.findItemRequest(userId, requestId));
    }
}