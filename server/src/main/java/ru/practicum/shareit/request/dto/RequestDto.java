package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemAnswerDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemAnswerDto> items;
}
