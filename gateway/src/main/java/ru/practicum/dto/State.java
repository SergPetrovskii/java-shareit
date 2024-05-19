package ru.practicum.dto;

import java.util.Optional;

public enum State {

    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    ALL;

    public static Optional<State> from(String stringState) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
