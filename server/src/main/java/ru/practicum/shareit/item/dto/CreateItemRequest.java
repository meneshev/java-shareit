package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class CreateItemRequest {
    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    public Boolean isRequestIdEmpty() {
        return requestId == null || requestId == 0;
    }
}
