package ru.practicum.booking.dto;

import lombok.*;
import ru.practicum.booking.model.Status;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingSearch {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime finish;

    private Status status;

    private Item item;

    private Long itemId;

    private User booker;
}
