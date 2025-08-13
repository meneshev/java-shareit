package ru.practicum.shareit.item.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private Set<CommentDto> comments;
}
