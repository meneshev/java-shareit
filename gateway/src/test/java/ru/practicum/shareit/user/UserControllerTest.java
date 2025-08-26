package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @SneakyThrows
    @Test
    void getUsersTest() {
        mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient).getUsers();
    }

    @SneakyThrows
    @Test
    void getUserTest() {
        Long userId = 1L;

        mockMvc.perform(get("/users/{user-id}", userId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient).getUser(userId);
    }

    @SneakyThrows
    @Test
    void createUserTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("test");
        createUserRequest.setEmail("test@test.com");

        String createdUser = "{\"id\":1,\"name\":\"test\",\"email\":\"test@test.com\"";

        when(userClient.createUser(createUserRequest)).thenReturn(ResponseEntity.ok(createdUser));

        String result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(createdUser, result);
    }

    @SneakyThrows
    @Test
    void updateUserTest() {
        Long userId = 1L;

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("test");

        String updatedUser = "{\"id\":1,\"name\":\"test\",\"email\":\"test@test.com\"";

        when(userClient.updateUser(userId, updateUserRequest)).thenReturn(ResponseEntity.ok(updatedUser));

        String result = mockMvc.perform(patch("/users/{user-id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(updatedUser, result);
    }

    @SneakyThrows
    @Test
    void deleteUserTest() {
        Long userId = 1L;

        String userDeleteResponse = "\"message\":\"User has been deleted\"";

        when(userClient.deleteUser(userId)).thenReturn(ResponseEntity.ok(userDeleteResponse));

        String result = mockMvc.perform(delete("/users/{user-id}", userId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(userDeleteResponse, result);
    }
}