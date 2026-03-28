package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestClient requestClient;

    @SneakyThrows
    @Test
    void getUserRequestsTest() {
        Long userId = 1L;
        String responseBody = "[{\"id\":1,\"description\":\"test request\",\"created\":\"2025-01-01T10:00:00\"}]";

        when(requestClient.getUserRequests(userId)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void getOtherUsersRequestsTest() {
        Long userId = 1L;
        String responseBody = "[{\"id\":2,\"description\":\"other user request\",\"created\":\"2024-01-01T11:00:00\"}]";

        when(requestClient.getOtherUsersRequests(userId)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void getRequestTest() {
        Long userId = 1L;
        Long requestId = 1L;
        String responseBody = "{\"id\":1,\"description\":\"test request\",\"created\":\"2024-01-01T10:00:00\"," +
                "\"items\":[]}";

        when(requestClient.getRequest(userId, requestId)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(get("/requests/{request-id}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void createRequestTest() {
        Long userId = 1L;
        CreateItemRequestReqDto createRequest = new CreateItemRequestReqDto();
        createRequest.setDescription("test request description");

        String responseBody = "{\"id\":1,\"description\":\"test request description\"," +
                "\"created\":\"2024-01-01T10:00:00\"}";

        when(requestClient.createRequest(userId, createRequest)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void getAllRequestsEndpointsWithDifferentUsersTest() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        String userRequestsResponse = "[{\"id\":1,\"description\":\"my request\"}]";
        String otherRequestsResponse = "[{\"id\":2,\"description\":\"other request\"}]";

        when(requestClient.getUserRequests(userId1)).thenReturn(ResponseEntity.ok(userRequestsResponse));
        when(requestClient.getOtherUsersRequests(userId2)).thenReturn(ResponseEntity.ok(otherRequestsResponse));

        String result1 = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(userRequestsResponse, result1);

        String result2 = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(otherRequestsResponse, result2);
    }
}