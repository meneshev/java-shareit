package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String name;

    private String email;
}
