package ru.practicum.shareit.util;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String message;
    private final String description;
    public ErrorResponse(String message, String description) {
        this.message = message;
        this.description = description;
    }
}
