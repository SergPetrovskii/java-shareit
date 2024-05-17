package ru.practicum.dto;

import lombok.*;
import ru.practicum.validators.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@StartBeforeEndDateValid
public class BookingDtoReceived {

    @NotNull(groups = {Marker.OnCreate.class})
    @FutureOrPresent(groups = {Marker.OnCreate.class})
    private LocalDateTime start;

    @NotNull(groups = {Marker.OnCreate.class})
    @Future(groups = {Marker.OnCreate.class})
    private LocalDateTime end;

    @NotNull(groups = {Marker.OnCreate.class})
    private Long itemId;
}
