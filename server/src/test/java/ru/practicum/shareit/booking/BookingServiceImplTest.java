package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingRepository bookingRepository;

    private UserDto ownerUser;
    private UserDto bookerUser;
    private ItemDto testItem;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();

        CreateUserRequest ownerRequest = new CreateUserRequest();
        ownerRequest.setName("Item Owner");
        ownerRequest.setEmail("owner@test.com");
        ownerUser = userService.createUser(ownerRequest);

        CreateUserRequest bookerRequest = new CreateUserRequest();
        bookerRequest.setName("Booker User");
        bookerRequest.setEmail("booker@test.com");
        bookerUser = userService.createUser(bookerRequest);

        CreateItemRequest itemRequest = new CreateItemRequest();
        itemRequest.setName("Bookable Item");
        itemRequest.setDescription("Item for booking");
        itemRequest.setAvailable(true);
        testItem = itemService.createItem(itemRequest, ownerUser.getId());
    }

    @Test
    void createBookingTest() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(testItem.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto createdBooking = bookingService.createBooking(request, bookerUser.getId());

        assertNotNull(createdBooking);
        assertNotNull(createdBooking.getId());
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());
        assertEquals(testItem.getId(), createdBooking.getItem().id());
        assertEquals(bookerUser.getId(), createdBooking.getBooker().id());

        Booking foundBooking = bookingRepository.findById(createdBooking.getId()).orElseThrow();
        assertEquals(BookingStatus.WAITING, foundBooking.getStatus());
    }

    @Test
    void createBookingWhenItemNotAvailable() {
        CreateItemRequest unavailableItemRequest = new CreateItemRequest();
        unavailableItemRequest.setName("Unavailable Item");
        unavailableItemRequest.setDescription("Description");
        unavailableItemRequest.setAvailable(false);
        ItemDto unavailableItem = itemService.createItem(unavailableItemRequest, ownerUser.getId());

        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(unavailableItem.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(request, bookerUser.getId()));
    }

    @Test
    void approveBookingTest() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(testItem.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.createBooking(request, bookerUser.getId());

        BookingDto approvedBooking = bookingService.approveBooking(
                createdBooking.getId(), ownerUser.getId(), true);

        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());

        Booking foundBooking = bookingRepository.findById(createdBooking.getId()).orElseThrow();
        assertEquals(BookingStatus.APPROVED, foundBooking.getStatus());
    }

    @Test
    void rejectBookingTest() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(testItem.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.createBooking(request, bookerUser.getId());

        BookingDto rejectedBooking = bookingService.approveBooking(
                createdBooking.getId(), ownerUser.getId(), false);

        assertEquals(BookingStatus.REJECTED, rejectedBooking.getStatus());
    }

    @Test
    void getBookingByIdWhenUserIsBookerOrOwner() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(testItem.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.createBooking(request, bookerUser.getId());

        BookingDto bookingByBooker = bookingService.getBookingById(
                createdBooking.getId(), bookerUser.getId());
        assertNotNull(bookingByBooker);
        assertEquals(createdBooking.getId(), bookingByBooker.getId());

        BookingDto bookingByOwner = bookingService.getBookingById(
                createdBooking.getId(), ownerUser.getId());
        assertNotNull(bookingByOwner);
        assertEquals(createdBooking.getId(), bookingByOwner.getId());
    }

    @Test
    void getBookingByIdWhenUserCannotViewBooking() {
        CreateUserRequest otherUserRequest = new CreateUserRequest();
        otherUserRequest.setName("Other User");
        otherUserRequest.setEmail("other@test.com");
        UserDto otherUser = userService.createUser(otherUserRequest);

        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(testItem.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.createBooking(request, bookerUser.getId());

        assertThrows(ValidationException.class,
                () -> bookingService.getBookingById(createdBooking.getId(), otherUser.getId()));
    }

    @Test
    void getBookingsByUserIdTest() {
        CreateBookingRequest request1 = new CreateBookingRequest();
        request1.setItemId(testItem.getId());
        request1.setStart(LocalDateTime.now().plusDays(1));
        request1.setEnd(LocalDateTime.now().plusDays(2));

        CreateBookingRequest request2 = new CreateBookingRequest();
        request2.setItemId(testItem.getId());
        request2.setStart(LocalDateTime.now().plusDays(3));
        request2.setEnd(LocalDateTime.now().plusDays(4));

        bookingService.createBooking(request1, bookerUser.getId());
        bookingService.createBooking(request2, bookerUser.getId());

        List<BookingDto> userBookings = bookingService.getBookingsByUserId(bookerUser.getId());

        assertEquals(2, userBookings.size());
        assertTrue(userBookings.stream().allMatch(
                booking -> booking.getBooker().id().equals(bookerUser.getId())));
    }

    @Test
    void getBookingsByOwnerIdTest() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(testItem.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createBooking(request, bookerUser.getId());

        List<BookingDto> ownerBookings = bookingService.getBookingsByOwnerId(ownerUser.getId());

        assertEquals(1, ownerBookings.size());
        assertEquals(testItem.getId(), ownerBookings.getFirst().getItem().id());
    }

    @Test
    void getBookingsByUserAndState() {
        CreateBookingRequest waitingRequest = new CreateBookingRequest();
        waitingRequest.setItemId(testItem.getId());
        waitingRequest.setStart(LocalDateTime.now().plusDays(1));
        waitingRequest.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto waitingBooking = bookingService.createBooking(waitingRequest, bookerUser.getId());

        List<BookingDto> allBookings = bookingService.getBookingsByUserAndState(
                bookerUser.getId(), BookingState.ALL);

        assertEquals(1, allBookings.size());
        assertEquals(waitingBooking.getId(), allBookings.getFirst().getId());
    }

    @Test
    void getBookingsByUserAndState_AllCurrentPastFutureWaitingRejected() {
        CreateBookingRequest pastReq = new CreateBookingRequest();
        pastReq.setItemId(testItem.getId());
        pastReq.setStart(LocalDateTime.now().minusDays(5));
        pastReq.setEnd(LocalDateTime.now().minusDays(3));
        bookingService.createBooking(pastReq, bookerUser.getId());

        CreateBookingRequest currReq = new CreateBookingRequest();
        currReq.setItemId(testItem.getId());
        currReq.setStart(LocalDateTime.now().minusHours(1));
        currReq.setEnd(LocalDateTime.now().plusHours(1));
        bookingService.createBooking(currReq, bookerUser.getId());
        CreateBookingRequest futReq = new CreateBookingRequest();
        futReq.setItemId(testItem.getId());
        futReq.setStart(LocalDateTime.now().plusDays(2));
        futReq.setEnd(LocalDateTime.now().plusDays(3));
        bookingService.createBooking(futReq, bookerUser.getId());

        List<BookingDto> all = bookingService.getBookingsByUserAndState(bookerUser.getId(), BookingState.ALL);
        List<BookingDto> past = bookingService.getBookingsByUserAndState(bookerUser.getId(), BookingState.PAST);
        List<BookingDto> fut = bookingService.getBookingsByUserAndState(bookerUser.getId(), BookingState.FUTURE);
        List<BookingDto> waiting = bookingService.getBookingsByUserAndState(bookerUser.getId(), BookingState.WAITING);

        assertEquals(3, all.size());
        assertEquals(1, past.size());
        assertEquals(1, fut.size());
        assertEquals(3, waiting.size());
    }

    @Test
    void getBookingsByOwnerAndState_AllCurrentPastFutureWaitingRejected() {
        // создаём бронирования от имени bookerUser
        CreateBookingRequest req = new CreateBookingRequest();
        req.setItemId(testItem.getId());
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createBooking(req, bookerUser.getId());

        List<BookingDto> all = bookingService.getBookingsByOwnerAndState(ownerUser.getId(), BookingState.ALL);
        List<BookingDto> waiting = bookingService.getBookingsByOwnerAndState(ownerUser.getId(), BookingState.WAITING);

        assertEquals(1, all.size());
        assertEquals(1, waiting.size());
    }

    @Test
    void validateCreateBooking_InvalidScenarios() {
        CreateBookingRequest sameOwnerReq = new CreateBookingRequest();
        sameOwnerReq.setItemId(testItem.getId());
        sameOwnerReq.setStart(LocalDateTime.now().plusDays(1));
        sameOwnerReq.setEnd(LocalDateTime.now().plusDays(2));
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(sameOwnerReq, ownerUser.getId()));

        CreateBookingRequest wrongDatesReq = new CreateBookingRequest();
        wrongDatesReq.setItemId(testItem.getId());
        wrongDatesReq.setStart(LocalDateTime.now().plusDays(5));
        wrongDatesReq.setEnd(LocalDateTime.now().plusDays(1));
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(wrongDatesReq, bookerUser.getId()));

        CreateBookingRequest equalDatesReq = new CreateBookingRequest();
        equalDatesReq.setItemId(testItem.getId());
        LocalDateTime point = LocalDateTime.now().plusDays(1);
        equalDatesReq.setStart(point);
        equalDatesReq.setEnd(point);
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(equalDatesReq, bookerUser.getId()));

        CreateItemRequest unavailable = new CreateItemRequest();
        unavailable.setName("X");
        unavailable.setDescription("Y");
        unavailable.setAvailable(false);
        ItemDto item2 = itemService.createItem(unavailable, ownerUser.getId());
        CreateBookingRequest unavailReq = new CreateBookingRequest();
        unavailReq.setItemId(item2.getId());
        unavailReq.setStart(LocalDateTime.now().plusDays(1));
        unavailReq.setEnd(LocalDateTime.now().plusDays(2));
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(unavailReq, bookerUser.getId()));
    }

    @Test
    void validateCreateBooking_UserNotFound() {
        CreateBookingRequest req = new CreateBookingRequest();
        req.setItemId(testItem.getId());
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));
        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(req, 999L));
    }
}