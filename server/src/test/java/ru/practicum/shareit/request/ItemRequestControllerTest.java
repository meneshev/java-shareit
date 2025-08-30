package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.util.ErrorHandler;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemRequestControllerTest {

    private MockMvc mockMvc;
    private ItemRequestService itemRequestService;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        itemRequestService = mock(ItemRequestService.class);
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ItemRequestController(itemRequestService))
                .setControllerAdvice(new ErrorHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

    @Test
    void getUserRequests_returnsOkAndList() throws Exception {
        RequestDto dto = new RequestDto();
        dto.setId(100L);
        when(itemRequestService.getRequestsByUser(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

        verify(itemRequestService).getRequestsByUser(1L);
    }

    @Test
    void getOtherUsersRequests_returnsOkAndList() throws Exception {
        RequestDto dto = new RequestDto();
        dto.setId(101L);
        when(itemRequestService.getRequestsByOtherUsers(2L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

        verify(itemRequestService).getRequestsByOtherUsers(2L);
    }

    @Test
    void getRequestById_returnsOkAndDto() throws Exception {
        RequestDto dto = new RequestDto();
        dto.setId(102L);
        when(itemRequestService.getRequestById(102L)).thenReturn(dto);

        mockMvc.perform(get("/requests/102")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(itemRequestService).getRequestById(102L);
    }

    @Test
    void createRequest_returnsCreatedAndDto() throws Exception {
        CreateItemRequestReqDto req = new CreateItemRequestReqDto();
        req.setDescription("Need drill");
        RequestDto dto = new RequestDto();
        dto.setId(103L);
        dto.setDescription("Need drill");
        dto.setCreated(LocalDateTime.now());
        dto.setItems(List.of());

        when(itemRequestService.saveRequest(eq(req), eq(4L))).thenReturn(dto);

        String s = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(itemRequestService).saveRequest(eq(req), eq(4L));
    }
}