package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserShort;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {
    public static BookingDto mapToDto(Booking booking, ItemShort item, UserShort user) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setBooker(user);
        bookingDto.setItem(item);
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static Booking mapToBooking(CreateBookingRequest request, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }
}
