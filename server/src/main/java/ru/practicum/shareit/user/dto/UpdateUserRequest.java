package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;

    private String email;

    public Boolean isNameEmpty() {
        return name == null || name.isEmpty();
    }

    public Boolean isEmailEmpty() {
        return email == null || email.isEmpty();
    }
}
