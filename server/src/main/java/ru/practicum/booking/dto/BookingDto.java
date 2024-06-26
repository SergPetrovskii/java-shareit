package ru.practicum.booking.dto;

import lombok.*;
import ru.practicum.booking.model.Status;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
public class BookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingDtoItem item;

    private BookingDtoBooker booker;

    private Status status;

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class BookingDtoItem {
        private Long id;
        private String name;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    public static class BookingDtoBooker {
        private Long id;
    }
}
