package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.ErrorHandler;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    private UserService userService;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userService))
                .setControllerAdvice(new ErrorHandler())
                .build();
        mapper = new ObjectMapper();
    }

    @Test
    void save_returnsCreatedAndUserDto() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setName("Alice");
        req.setEmail("alice@example.com");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Alice");
        dto.setEmail("alice@example.com");

        when(userService.createUser(eq(req))).thenReturn(dto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(userService).createUser(eq(req));
    }

    @Test
    void update_returnsOkAndUserDto() throws Exception {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setName("Bob");
        req.setEmail("bob@example.com");

        UserDto dto = new UserDto();
        dto.setId(2L);
        dto.setName("Bob");
        dto.setEmail("bob@example.com");

        when(userService.updateUser(eq(req), eq(2L))).thenReturn(dto);

        mockMvc.perform(patch("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(userService).updateUser(eq(req), eq(2L));
    }

    @Test
    void deleteUser_returnsOkAndMessage() throws Exception {
        doNothing().when(userService).deleteUser(3L);

        mockMvc.perform(delete("/users/3"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Map.of("message", "User has been deleted"))));

        verify(userService).deleteUser(3L);
    }

    @Test
    void getUserById_returnsOkAndUserDto() throws Exception {
        UserDto dto = new UserDto();
        dto.setId(4L);
        dto.setName("Carol");
        dto.setEmail("carol@example.com");

        when(userService.getUserDtoById(4L)).thenReturn(dto);

        mockMvc.perform(get("/users/4"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(userService).getUserDtoById(4L);
    }

    @Test
    void getAllUsers_returnsOkAndList() throws Exception {
        UserDto dto1 = new UserDto();
        dto1.setId(5L);
        dto1.setName("Dave");
        dto1.setEmail("dave@example.com");

        UserDto dto2 = new UserDto();
        dto2.setId(6L);
        dto2.setName("Eve");
        dto2.setEmail("eve@example.com");

        when(userService.getAllUsers()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto1, dto2))));

        verify(userService).getAllUsers();
    }
}