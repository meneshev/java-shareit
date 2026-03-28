package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @SneakyThrows
    @Test
    void getBookingTest() {
        Long userId = 1L;
        Long bookingId = 1L;
        String responseBody = "{\"id\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-01T12:00:00\"," +
                "\"status\":\"APPROVED\"}";

        when(bookingClient.getBooking(userId, bookingId)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void getBookingsTest() {
        Long userId = 1L;
        String stateParam = "ALL";
        Integer from = 0;
        Integer size = 10;
        String responseBody = "[{\"id\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-01T12:00:00\"," +
                "\"status\":\"APPROVED\"}]";

        when(bookingClient.getBookings(userId, BookingState.ALL, from, size, false))
                .thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateParam)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void getBookingsWithDefaultParametersTest() {
        Long userId = 1L;
        String responseBody = "[{\"id\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-01T12:00:00\"," +
                "\"status\":\"APPROVED\"}]";

        when(bookingClient.getBookings(userId, BookingState.ALL, 0, 10, false))
                .thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void getOwnerBookingsTest() {
        Long userId = 1L;
        String stateParam = "WAITING";
        Integer from = 5;
        Integer size = 20;
        String responseBody = "[{\"id\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-01T12:00:00\"," +
                "\"status\":\"WAITING\"}]";

        when(bookingClient.getBookings(userId, BookingState.WAITING, from, size, true))
                .thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateParam)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void bookItemTest() {
        Long userId = 1L;
        CreateBookingRequest createBookingRequest = new CreateBookingRequest();
        createBookingRequest.setItemId(1L);
        createBookingRequest.setStart(LocalDateTime.of(2026, 1, 1, 10, 0));
        createBookingRequest.setEnd(LocalDateTime.of(2026, 1, 2, 12, 0));

        String responseBody = "{\"id\":1,\"start\":\"2026-01-01T10:00:00\",\"end\":\"2026-02-01T12:00:00\"," +
                "\"status\":\"WAITING\"}";

        when(bookingClient.bookItem(userId, createBookingRequest)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookingRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void approveBookingTest() {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;
        String responseBody = "{\"id\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-01T12:00:00\"," +
                "\"status\":\"APPROVED\"}";

        when(bookingClient.approveBooking(userId, bookingId, approved)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(patch("/bookings/{booking-id}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", approved.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
        verify(bookingClient).approveBooking(userId, bookingId, approved);
    }
}