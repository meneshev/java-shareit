
package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserService userService;

    private UserDto userA;
    private UserDto userB;

    @BeforeEach
    void setUp() {
        requestRepository.deleteAll();
        CreateUserRequest ua = new CreateUserRequest();
        ua.setName("User A");
        ua.setEmail("a@test.com");
        userA = userService.createUser(ua);

        CreateUserRequest ub = new CreateUserRequest();
        ub.setName("User B");
        ub.setEmail("b@test.com");
        userB = userService.createUser(ub);
    }

    @Test
    void saveRequestAndGetByIdTest() {
        CreateItemRequestReqDto createDto = new CreateItemRequestReqDto();
        createDto.setDescription("Need a drill");
        RequestDto saved = requestService.saveRequest(createDto, userA.getId());

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Need a drill", saved.getDescription());
        assertNotNull(saved.getCreated());

        RequestDto found = requestService.getRequestById(saved.getId());
        assertEquals(saved.getId(), found.getId());
        assertEquals("Need a drill", found.getDescription());
    }

    @Test
    void getRequestsByUserReturnsOwnTest() {
        CreateItemRequestReqDto dto1 = new CreateItemRequestReqDto();
        dto1.setDescription("First");
        RequestDto r1 = requestService.saveRequest(dto1, userA.getId());

        CreateItemRequestReqDto dto2 = new CreateItemRequestReqDto();
        dto2.setDescription("Second");
        RequestDto r2 = requestService.saveRequest(dto2, userA.getId());

        List<RequestDto> list = requestService.getRequestsByUser(userA.getId());
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(r -> r.getId().equals(r1.getId())));
        assertTrue(list.stream().anyMatch(r -> r.getId().equals(r2.getId())));
    }

    @Test
    void getRequestsByOtherUsersExcludesOwnTest() {
        CreateItemRequestReqDto dtoA = new CreateItemRequestReqDto();
        dtoA.setDescription("From A");
        RequestDto ra = requestService.saveRequest(dtoA, userA.getId());

        CreateItemRequestReqDto dtoB = new CreateItemRequestReqDto();
        dtoB.setDescription("From B");
        RequestDto rb = requestService.saveRequest(dtoB, userB.getId());

        List<RequestDto> othersForA = requestService.getRequestsByOtherUsers(userA.getId());
        assertEquals(1, othersForA.size());
        assertEquals(rb.getId(), othersForA.get(0).getId());

        List<RequestDto> othersForB = requestService.getRequestsByOtherUsers(userB.getId());
        assertEquals(1, othersForB.size());
        assertEquals(ra.getId(), othersForB.get(0).getId());
    }
}