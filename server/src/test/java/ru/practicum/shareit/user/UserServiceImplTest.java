package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUserTest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Integration Test User");
        request.setEmail("integration@test.com");

        UserDto createdUser = userService.createUser(request);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("Integration Test User", createdUser.getName());
        assertEquals("integration@test.com", createdUser.getEmail());

        UserDto foundUser = userService.getUserDtoById(createdUser.getId());
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals("Integration Test User", foundUser.getName());
    }

    @Test
    void updateUserTest() {
        CreateUserRequest createRequest = new CreateUserRequest();
        createRequest.setName("Original Name");
        createRequest.setEmail("original@test.com");
        UserDto createdUser = userService.createUser(createRequest);

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("updated@test.com");

        UserDto updatedUser = userService.updateUser(updateRequest, createdUser.getId());

        assertNotNull(updatedUser);
        assertEquals(createdUser.getId(), updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@test.com", updatedUser.getEmail());

        UserDto foundUser = userService.getUserDtoById(createdUser.getId());
        assertEquals("Updated Name", foundUser.getName());
        assertEquals("updated@test.com", foundUser.getEmail());
    }

    @Test
    void updateUserWithOldDataTest() {
        CreateUserRequest createRequest = new CreateUserRequest();
        createRequest.setName("Original Name");
        createRequest.setEmail("original@test.com");
        UserDto createdUser = userService.createUser(createRequest);

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("updated@test.com");

        UserDto updatedUser = userService.updateUser(updateRequest, createdUser.getId());

        assertNotNull(updatedUser);
        assertEquals(createdUser.getId(), updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@test.com", updatedUser.getEmail());

        updateRequest.setName(null);
        updatedUser = userService.updateUser(updateRequest, createdUser.getId());
        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());

        updateRequest.setEmail(null);
        updatedUser = userService.updateUser(updateRequest, createdUser.getId());
        assertNotNull(updatedUser);
        assertEquals("updated@test.com", updatedUser.getEmail());

        UserDto foundUser = userService.getUserDtoById(createdUser.getId());
        assertEquals("Updated Name", foundUser.getName());
        assertEquals("updated@test.com", foundUser.getEmail());
    }

    @Test
    void getAllUsersTest() {
        CreateUserRequest request1 = new CreateUserRequest();
        request1.setName("User 1");
        request1.setEmail("user1@test.com");

        CreateUserRequest request2 = new CreateUserRequest();
        request2.setName("User 2");
        request2.setEmail("user2@test.com");

        userService.createUser(request1);
        userService.createUser(request2);

        List<UserDto> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("User 1")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("User 2")));
    }

    @Test
    void deleteUserTest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("User To Delete");
        request.setEmail("delete@test.com");
        UserDto createdUser = userService.createUser(request);

        userService.deleteUser(createdUser.getId());

        assertThrows(NotFoundException.class,
                () -> userService.getUserDtoById(createdUser.getId()));
    }
}