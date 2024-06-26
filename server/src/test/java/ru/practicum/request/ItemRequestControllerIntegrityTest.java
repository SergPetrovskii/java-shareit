package ru.practicum.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.request.controller.ItemRequestController;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestDtoReceived;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.model.ItemRequestWithItems;
import ru.practicum.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerIntegrityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private long itemRequestId = 1L;

    private long userId = 1L;

    private ItemRequest itemRequest = ItemRequest.builder()
            .created(LocalDateTime.now())
            .userId(userId)
            .id(itemRequestId)
            .description("asd")
            .build();

    private ItemRequestWithItems itemRequestWithItems = ItemRequestWithItems.builder()
            .items(List.of())
            .description("asd")
            .created(LocalDateTime.now())
            .id(1L)
            .build();

    @SneakyThrows
    @Test
    void addRequest() {
        ItemRequestDtoReceived itemRequestDtoReceived = ItemRequestDtoReceived.builder()
                .description("asd")
                .userId(userId)
                .build();

        ItemRequestDto itemRequest1 = RequestMapper.toRequestDto(itemRequest);

        when(itemRequestService.addRequest(any(), any())).thenReturn(itemRequest);

        String requestString = mockMvc.perform(post("/requests", itemRequestDtoReceived)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(requestString, objectMapper.writeValueAsString(itemRequest1));
    }

    @SneakyThrows
    @Test
    void findListRequestUser() {

        when(itemRequestService.findListRequestUser(userId)).thenReturn(List.of(itemRequestWithItems));

        String requestString = mockMvc.perform(get("/requests")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestWithItems))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(requestString, objectMapper.writeValueAsString(List.of(itemRequestWithItems)));
    }

    @SneakyThrows
    @Test
    void findListRequest() {

        when(itemRequestService.findListRequest(userId, 0, 10)).thenReturn(List.of(itemRequestWithItems));

        String requestString = mockMvc.perform(get("/requests/all")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestWithItems))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(requestString, objectMapper.writeValueAsString(List.of(itemRequestWithItems)));
    }

    @SneakyThrows
    @Test
    void findItemRequest() {

        when(itemRequestService.findItemRequest(userId, itemRequestId)).thenReturn(itemRequestWithItems);

        String requestString = mockMvc.perform(get("/requests/{requestId}", itemRequestId)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestWithItems))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(requestString, objectMapper.writeValueAsString(itemRequestWithItems));
    }

    @SneakyThrows
    @Test
    void addRequestBadRequest() {
        ItemRequestDtoReceived itemRequestDtoReceived = ItemRequestDtoReceived.builder()
                .userId(userId)
                .build();

        when(itemRequestService.addRequest(any(), any())).thenReturn(itemRequest);

        mockMvc.perform(post("/requests", itemRequestDtoReceived)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestDtoReceived))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
