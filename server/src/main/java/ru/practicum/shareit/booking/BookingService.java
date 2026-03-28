package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import java.util.List;

public interface BookingService {
    BookingDto createBooking(CreateBookingRequest request, Long userId);

    BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByUserId(Long userId);

    List<BookingDto> getBookingsByOwnerId(Long userId);

    List<BookingDto> getBookingsByUserAndState(Long userId, BookingState bookingState);

    List<BookingDto> getBookingsByOwnerAndState(Long userId, BookingState bookingState);
}
