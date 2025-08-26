package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateItemRequestReqDto {
    @NotNull
    @NotEmpty
    private String description;
}
