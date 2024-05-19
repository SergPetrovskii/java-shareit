package ru.practicum.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommentDtoReceived {

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(min = 1, max = 100, groups = {Marker.OnCreate.class})
    private String text;
}
