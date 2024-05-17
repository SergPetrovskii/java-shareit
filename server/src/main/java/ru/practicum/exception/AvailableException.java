package ru.practicum.exception;

public class AvailableException extends RuntimeException {
    public AvailableException(final String message) {
        super(message);
    }
}
