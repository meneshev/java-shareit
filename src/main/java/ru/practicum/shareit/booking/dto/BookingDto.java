package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemShort;
import ru.practicum.shareit.user.UserShort;
import java.time.LocalDateTime;

@Data
public class BookingDto {
    @NotNull
    private Long id;

    @NotNull
    private UserShort booker;

    @NotNull
    private ItemShort item;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private BookingStatus status;
}