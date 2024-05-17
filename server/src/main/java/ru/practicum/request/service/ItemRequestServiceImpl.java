package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.item.dao.ItemRepository;
import ru.practicum.item.model.Item;
import ru.practicum.request.dao.ItemRequestRepository;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.model.ItemRequestWithItems;
import ru.practicum.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequest addRequest(ItemRequest request, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        request.setUserId(userId);
        request.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(request);
    }

    @Override
    public List<ItemRequestWithItems> findListRequest(long userId, int from, int size) {
        checkUser(userId);

        Pageable pageable = PageRequest.of(from, size, Sort.by("created").ascending());

        List<ItemRequestWithItems> listRequestWithItems = RequestMapper
                .toListItemRequestWithItemsFromItemRequestSearch(itemRequestRepository.findAllByUserIdNotOrderByCreated(userId, pageable));

        List<Long> listRequestId = listRequestWithItems.stream().map(ItemRequestWithItems::getId).collect(Collectors.toList());

        List<Item> allByRequestIds = itemRepository.findAllByRequestIds(listRequestId);

        addItems(allByRequestIds, listRequestWithItems);

        return listRequestWithItems;

    }

    @Override
    public List<ItemRequestWithItems> findListRequestUser(long userId) {
        checkUser(userId);

        List<ItemRequestWithItems> listRequestWithItems =
                RequestMapper.toListItemRequestWithItemsFromItemRequestSearch(itemRequestRepository.findAllByUserId(userId));

        List<Long> listRequestId = listRequestWithItems.stream().map(ItemRequestWithItems::getId).collect(Collectors.toList());

        List<Item> allByRequestIds = itemRepository.findAllByRequestIds(listRequestId);

        addItems(allByRequestIds, listRequestWithItems);

        return listRequestWithItems;
    }

    @Override
    public ItemRequestWithItems findItemRequest(long userId, long requestId) {
        checkUser(userId);

        ItemRequestWithItems request = RequestMapper
                .toItemRequestWithItemsFromItemRequestSearch(itemRequestRepository.findById(requestId)
                        .orElseThrow(() -> new EntityNotFoundException("Request not found")));

        List<Item> listItem = itemRepository.findAllByRequestId(requestId);

        request.setItems(listItem);
        return request;
    }

    private void checkUser(long userId) {
        boolean answer = userRepository.existsById(userId);
        if (!answer) {
            throw new EntityNotFoundException("User not found");
        }
    }

    private void addItems(List<Item> allByRequestIds, List<ItemRequestWithItems> listRequestWithItems) {
        if (!allByRequestIds.isEmpty()) {
            Map<Long, List<Item>> mapItem = allByRequestIds.stream()
                    .collect(Collectors.groupingBy(item -> item.getRequestId(), Collectors.toList()));

            listRequestWithItems.stream()
                    .forEach(request -> {
                        List<Item> listItem = mapItem.getOrDefault(request.getId(), List.of());

                        request.setItems(listItem);
                    });
        } else {
            listRequestWithItems.stream()
                    .forEach(a -> a.setItems(List.of()));
        }
    }
}
