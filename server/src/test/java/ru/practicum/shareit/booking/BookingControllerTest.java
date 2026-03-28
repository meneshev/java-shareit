package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.util.ErrorHandler;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingControllerTest {

    private MockMvc mockMvc;
    private BookingService bookingService;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        bookingService = mock(BookingService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new BookingController(bookingService))
                .setControllerAdvice(new ErrorHandler())
                .build();
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void createBooking_returnsCreatedAndDto() throws Exception {
        CreateBookingRequest req = new CreateBookingRequest();
        req.setItemId(1L);
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto dto = new BookingDto();
        dto.setId(10L);
        when(bookingService.createBooking(eq(req), eq(5L))).thenReturn(dto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(bookingService).createBooking(eq(req), eq(5L));
    }

    @Test
    void approveBooking_withValidApproved_returnsOkAndDto() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(20L);
        when(bookingService.approveBooking(15L, 7L, true)).thenReturn(dto);

        mockMvc.perform(patch("/bookings/15")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(bookingService).approveBooking(15L, 7L, true);
    }

    @Test
    void getBooking_returnsOkAndDto() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(30L);
        when(bookingService.getBookingById(30L, 8L)).thenReturn(dto);

        mockMvc.perform(get("/bookings/30")
                        .header("X-Sharer-User-Id", 8L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(bookingService).getBookingById(30L, 8L);
    }

    @Test
    void getBookingsByCurrentUser_withValidState_returnsList() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(40L);
        when(bookingService.getBookingsByUserAndState(9L, BookingState.WAITING))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .param("state", "waiting")
                        .header("X-Sharer-User-Id", 9L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

        verify(bookingService).getBookingsByUserAndState(9L, BookingState.WAITING);
    }

    @Test
    void getBookingsByCurrentUser_withBlankState_usesDefaultAll() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(50L);
        when(bookingService.getBookingsByUserAndState(10L, BookingState.ALL))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .param("state", "")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

        verify(bookingService).getBookingsByUserAndState(10L, BookingState.ALL);
    }

    @Test
    void getBookingsByCurrentUser_withInvalidState_throwsValidation() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("state", "invalid")
                        .header("X-Sharer-User-Id", 11L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    void getBookingsByOwner_withValidState_returnsList() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(60L);
        when(bookingService.getBookingsByOwnerAndState(12L, BookingState.CURRENT))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "current")
                        .header("X-Sharer-User-Id", 12L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

        verify(bookingService).getBookingsByOwnerAndState(12L, BookingState.CURRENT);
    }

    @Test
    void getBookingsByOwner_withEmptyState_usesDefaultAll() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(70L);
        when(bookingService.getBookingsByOwnerAndState(13L, BookingState.ALL))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "")
                        .header("X-Sharer-User-Id", 13L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

        verify(bookingService).getBookingsByOwnerAndState(13L, BookingState.ALL);
    }

    @Test
    void getBookingsByOwner_withNoParam_usesDefaultAll() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(80L);
        when(bookingService.getBookingsByOwnerAndState(14L, BookingState.ALL))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 14L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

        verify(bookingService).getBookingsByOwnerAndState(14L, BookingState.ALL);
    }

    @Test
    void getBookingsByOwner_withInvalidState_throwsValidation() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "bad")
                        .header("X-Sharer-User-Id", 15L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }
}
