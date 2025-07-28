package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class UpdateItemRequest {
    private String name;
    private String description;
    private Boolean available;

    public Boolean isNameEmpty() {
        return name == null || name.isEmpty();
    }

    public Boolean isDescriptionEmpty() {
        return description == null || description.isEmpty();
    }

    public Boolean isAvailableEmpty() {
        return available == null;
    }
}
