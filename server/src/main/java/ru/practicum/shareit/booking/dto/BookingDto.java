package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemShort;
import ru.practicum.shareit.user.UserShort;
import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;

    private UserShort booker;

    private ItemShort item;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}