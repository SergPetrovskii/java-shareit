package ru.practicum.exception;

import lombok.Data;

@Data
public class ErrorResponseException {
    private String error;
    private String description;

    public ErrorResponseException(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public ErrorResponseException(String error) {
        this.error = error;
    }
}