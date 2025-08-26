package ru.practicum.shareit.util;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String message;
    private final String error;

    public ErrorResponse(String message, String error) {
        this.message = message;
        this.error = error;
    }
}
