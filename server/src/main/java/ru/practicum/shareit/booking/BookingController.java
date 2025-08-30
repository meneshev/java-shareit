package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.ValidationException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody CreateBookingRequest request,
                                    @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(request, userId);
    }

    @PatchMapping("/{booking-id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approveBooking(@PathVariable("booking-id") Long bookingId,
                                     @RequestParam(name = "approved") Boolean approved,
                                     @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{booking-id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBooking(@PathVariable("booking-id") Long bookingId,
                                 @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookingsByCurrentUser(@RequestParam(required = false, name = "state") String bookingState,
                                                     @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        if (bookingState != null && !bookingState.isBlank()) {
            try {
                BookingState state = BookingState.valueOf(bookingState.toUpperCase());
                return bookingService.getBookingsByUserAndState(userId, state);
            } catch (IllegalArgumentException e) {
                throw new ValidationException(
                        String.format("Передан некорректный параметр state. Допустимые значения:%s",
                                Arrays.toString(BookingState.values()).toLowerCase()));
            }
        }

        return bookingService.getBookingsByUserAndState(userId, BookingState.ALL);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookingsByOwner(@RequestParam(required = false, name = "state") String bookingState,
                                               @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        if (bookingState != null && !bookingState.isBlank()) {
            try {
                BookingState state = BookingState.valueOf(bookingState.toUpperCase());
                return bookingService.getBookingsByOwnerAndState(userId, state);
            } catch (IllegalArgumentException e) {
                throw new ValidationException(
                        String.format("Передан некорректный параметр state. Допустимые значения:%s",
                                Arrays.toString(BookingState.values()).toLowerCase()));
            }
        }

        return bookingService.getBookingsByOwnerAndState(userId, BookingState.ALL);
    }
}
