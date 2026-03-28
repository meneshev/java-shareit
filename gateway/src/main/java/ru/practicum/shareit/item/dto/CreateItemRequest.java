package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateItemRequest {
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;

    private Long requestId;

    public Boolean isRequestIdEmpty() {
        return requestId == null || requestId == 0;
    }
}
