package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private UserDto ownerUser;
    private UserDto regularUser;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        commentRepository.deleteAll();
        itemRepository.deleteAll();

        CreateUserRequest ownerRequest = new CreateUserRequest();
        ownerRequest.setName("Item Owner");
        ownerRequest.setEmail("owner@test.com");
        ownerUser = userService.createUser(ownerRequest);

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setName("Regular User");
        userRequest.setEmail("user@test.com");
        regularUser = userService.createUser(userRequest);
    }

    @Test
    void createItemTest() {
        CreateItemRequest request = new CreateItemRequest();
        request.setName("Test Item");
        request.setDescription("Test Description");
        request.setAvailable(true);

        ItemDto createdItem = itemService.createItem(request, ownerUser.getId());

        assertNotNull(createdItem);
        assertNotNull(createdItem.getId());
        assertEquals("Test Item", createdItem.getName());
        assertEquals("Test Description", createdItem.getDescription());
        assertTrue(createdItem.getAvailable());

        Item foundItem = itemService.getItemById(createdItem.getId());
        assertEquals("Test Item", foundItem.getName());
        assertEquals(ownerUser.getId(), foundItem.getOwner().getId());
    }

    @Test
    void updateItemTest() {
        CreateItemRequest createRequest = new CreateItemRequest();
        createRequest.setName("Original Name");
        createRequest.setDescription("Original Description");
        createRequest.setAvailable(true);
        ItemDto createdItem = itemService.createItem(createRequest, ownerUser.getId());

        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setDescription("Updated Description");
        updateRequest.setAvailable(false);

        ItemDto updatedItem = itemService.updateItem(updateRequest, ownerUser.getId(), createdItem.getId());

        assertEquals(createdItem.getId(), updatedItem.getId());
        assertEquals("Updated Name", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void getItemDtoByIdTest() {
        CreateItemRequest request = new CreateItemRequest();
        request.setName("Bookable Item");
        request.setDescription("Test Item");
        request.setAvailable(true);
        ItemDto createdItem = itemService.createItem(request, ownerUser.getId());

        Item item = itemService.getItemById(createdItem.getId());
        User booker = userService.getUserById(regularUser.getId());

        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        futureBooking.setStatus(BookingStatus.APPROVED);
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        bookingRepository.save(futureBooking);

        ItemDto result = itemService.getItemDtoById(createdItem.getId(), ownerUser.getId());

        assertNotNull(result);
        assertEquals(createdItem.getId(), result.getId());
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
    }

    @Test
    void getItemDtoByIdWhenUserIsNotOwner() {
        CreateItemRequest request = new CreateItemRequest();
        request.setName("Test Item");
        request.setDescription("Test Description");
        request.setAvailable(true);
        ItemDto createdItem = itemService.createItem(request, ownerUser.getId());

        ItemDto result = itemService.getItemDtoById(createdItem.getId(), regularUser.getId());

        assertNotNull(result);
        assertEquals(createdItem.getId(), result.getId());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void getItemsByUserIdTest() {
        CreateItemRequest request1 = new CreateItemRequest();
        request1.setName("Item 1");
        request1.setDescription("Description 1");
        request1.setAvailable(true);

        CreateItemRequest request2 = new CreateItemRequest();
        request2.setName("Item 2");
        request2.setDescription("Description 2");
        request2.setAvailable(false);

        itemService.createItem(request1, ownerUser.getId());
        itemService.createItem(request2, ownerUser.getId());

        List<ItemDto> userItems = itemService.getItemsByUserId(ownerUser.getId());

        assertEquals(2, userItems.size());
        assertTrue(userItems.stream().anyMatch(item -> item.getName().equals("Item 1")));
        assertTrue(userItems.stream().anyMatch(item -> item.getName().equals("Item 2")));
    }

    @Test
    void findItemsTest() {
        CreateItemRequest request1 = new CreateItemRequest();
        request1.setName("Drill Tool");
        request1.setDescription("Power drill for home use");
        request1.setAvailable(true);

        CreateItemRequest request2 = new CreateItemRequest();
        request2.setName("Hammer");
        request2.setDescription("Heavy duty hammer");
        request2.setAvailable(true);

        CreateItemRequest request3 = new CreateItemRequest();
        request3.setName("Electric Drill");
        request3.setDescription("Professional electric drill");
        request3.setAvailable(true);

        itemService.createItem(request1, ownerUser.getId());
        itemService.createItem(request2, ownerUser.getId());
        itemService.createItem(request3, ownerUser.getId());

        List<ItemDto> foundItems = itemService.findItems("drill", regularUser.getId());

        assertEquals(2, foundItems.size());
        assertTrue(foundItems.stream().anyMatch(item -> item.getName().contains("Drill")));
        assertTrue(foundItems.stream().noneMatch(item -> item.getName().equals("Hammer")));
    }

    @Test
    void createCommentTest() {
        CreateItemRequest itemRequest = new CreateItemRequest();
        itemRequest.setName("Commented Item");
        itemRequest.setDescription("Item for comments");
        itemRequest.setAvailable(true);
        ItemDto createdItem = itemService.createItem(itemRequest, ownerUser.getId());

        Item item = itemService.getItemById(createdItem.getId());
        User booker = userService.getUserById(regularUser.getId());

        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        bookingRepository.save(pastBooking);

        CreateCommentRequest commentRequest = new CreateCommentRequest();
        commentRequest.setText("Great item!");

        CommentDto createdComment = itemService.createComment(commentRequest,
                createdItem.getId(), regularUser.getId());

        assertNotNull(createdComment);
        assertNotNull(createdComment.getId());
        assertEquals("Great item!", createdComment.getText());
        assertNotNull(createdComment.getCreated());

        Comment foundComment = commentRepository.findById(createdComment.getId()).orElseThrow();
        assertEquals("Great item!", foundComment.getText());
    }

    @Test
    void deleteItemTest() {
        CreateItemRequest request = new CreateItemRequest();
        request.setName("Item to Delete");
        request.setDescription("Description");
        request.setAvailable(true);
        ItemDto createdItem = itemService.createItem(request, ownerUser.getId());

        itemService.deleteItem(createdItem.getId(), ownerUser.getId());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(createdItem.getId()));
    }
}