package ru.practicum.exception;

public class EmailDuplicateException extends RuntimeException {
    public EmailDuplicateException(final String message) {
        super(message);
    }
}