package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemServiceImpl;
    private final UserService userServiceImpl;

    @Override
    @Transactional
    public BookingDto createBooking(CreateBookingRequest request, Long userId) {
        validateCreateBooking(request, userId);

        Item item = itemServiceImpl.getItemById(request.getItemId());
        User user = userServiceImpl.getUserById(userId);
        Booking booking = bookingRepository.save(BookingMapper.mapToBooking(request, item, user));

        return BookingMapper.mapToDto(booking, new ItemShort(item.getId(), item.getName()),
                new UserShort(userId));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        try {
            userServiceImpl.checkUserId(userId);
        } catch (NotFoundException e) {
            throw new ValidationException("User not found");
        }


        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        itemServiceImpl.validateItem(booking.getItem().getId(), userId);

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.mapToDto(bookingRepository.save(booking),
                new ItemShort(booking.getItem().getId(),  booking.getItem().getName()),
                new UserShort(booking.getBooker().getId()));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        validateViewBooking(booking, userId);

        return BookingMapper.mapToDto(booking, new ItemShort(booking.getItem().getId(),  booking.getItem().getName()),
                new UserShort(userId));
    }

    @Override
    public List<BookingDto> getBookingsByUserId(Long userId) {
        userServiceImpl.getUserById(userId);
        return bookingRepository.findByBookerId(userId);
    }

    @Override
    public List<BookingDto> getBookingsByOwnerId(Long userId) {
        userServiceImpl.getUserById(userId);
        return bookingRepository.findByOwnerId(userId);
    }

    @Override
    public List<BookingDto> getBookingsByUserAndState(Long userId, BookingState bookingState) {
        userServiceImpl.getUserById(userId);
        return switch (bookingState) {
            case ALL -> bookingRepository.findByBookerId(userId);
            case CURRENT -> bookingRepository.getCurrentBookingsByBookerId(userId);
            case PAST -> bookingRepository.getPastBookingsByBookerId(userId);
            case FUTURE -> bookingRepository.getFutureBookingsByBookerId(userId);
            case WAITING -> bookingRepository.findByBookerIdAndStatusId(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusId(userId, BookingStatus.REJECTED);
        };
    }

    @Override
    public List<BookingDto> getBookingsByOwnerAndState(Long userId, BookingState bookingState) {
        userServiceImpl.getUserById(userId);
        return switch (bookingState) {
            case ALL -> bookingRepository.findByOwnerId(userId);
            case CURRENT -> bookingRepository.getCurrentBookingsByOwnerId(userId);
            case PAST -> bookingRepository.getPastBookingsByOwnerId(userId);
            case FUTURE -> bookingRepository.getFutureBookingsByOwnerId(userId);
            case WAITING -> bookingRepository.findByOwnerIdAndStatusId(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByOwnerIdAndStatusId(userId, BookingStatus.REJECTED);
        };
    }

    private void validateCreateBooking(CreateBookingRequest request, Long bookerUserId) {
        userServiceImpl.checkUserId(bookerUserId);
        Item item = itemServiceImpl.getItemById(request.getItemId());
        Long ownerId = item.getOwner().getId();

        if (!item.getIsAvailable()) {
            throw new ValidationException("Item is not available");
        }

        if (ownerId.equals(bookerUserId)) {
            throw new ValidationException("owner must not be the same as booker");
        }

        if (request.getStart().isAfter(request.getEnd())) {
            throw new ValidationException("`start` must be before `end`");
        }

        if (request.getStart().equals(request.getEnd())) {
            throw new ValidationException("`start` must not be equals `end`");
        }
    }

    private void validateViewBooking(Booking booking, Long userId) {
        if (!Objects.equals(userId, booking.getBooker().getId()) &&
                !Objects.equals(userId, booking.getItem().getOwner().getId())) {
            throw new ValidationException("Restrict access");
        }
    }
}
