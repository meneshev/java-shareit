package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.util.ErrorHandler;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemControllerTest {

    private MockMvc mockMvc;
    private ItemService itemService;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        itemService = mock(ItemService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ItemController(itemService))
                .setControllerAdvice(new ErrorHandler())
                .build();
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void save_returnsCreatedAndItemDto() throws Exception {
        CreateItemRequest req = new CreateItemRequest();
        req.setName("Item");
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        when(itemService.createItem(eq(req), eq(2L))).thenReturn(dto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(itemService).createItem(eq(req), eq(2L));
    }

    @Test
    void saveComment_returnsCreatedAndCommentDto() throws Exception {
        CreateCommentRequest req = new CreateCommentRequest();
        req.setText("Nice");
        CommentDto dto = new CommentDto();
        dto.setId(10L);

        when(itemService.createComment(eq(req), eq(3L), eq(5L))).thenReturn(dto);

        mockMvc.perform(post("/items/3/comment")
                        .header("X-Sharer-User-Id", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(itemService).createComment(eq(req), eq(3L), eq(5L));
    }

    @Test
    void update_returnsOkAndItemDto() throws Exception {
        UpdateItemRequest req = new UpdateItemRequest();
        req.setDescription("Desc");
        ItemDto dto = new ItemDto();
        dto.setId(20L);
        when(itemService.updateItem(eq(req), eq(4L), eq(6L))).thenReturn(dto);

        mockMvc.perform(patch("/items/6")
                        .header("X-Sharer-User-Id", 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(itemService).updateItem(eq(req), eq(4L), eq(6L));
    }

    @Test
    void delete_returnsOkAndMessage() throws Exception {
        doNothing().when(itemService).deleteItem(7L, 8L);

        mockMvc.perform(delete("/items/7")
                        .header("X-Sharer-User-Id", 8L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Map.of("message", "Item has been deleted"))));

        verify(itemService).deleteItem(7L, 8L);
    }

    @Test
    void getItemById_returnsOkAndItemDto() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(30L);
        dto.setName("Item");
        dto.setDescription("Desc");
        dto.setAvailable(true);

        when(itemService.getItemDtoById(3L, 9L)).thenReturn(dto);

        mockMvc.perform(get("/items/3")
                        .header("X-Sharer-User-Id", 9L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));

        verify(itemService).getItemDtoById(3L, 9L);
    }

    @Test
    void getItemsByUserId_returnsOkAndList() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(40L);
        when(itemService.getItemsByUserId(11L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 11L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(dto))));

        verify(itemService).getItemsByUserId(11L);
    }

    @Test
    void search_returnsOkAndList() throws Exception {
        when(itemService.findItems("test", 12L)).thenReturn(List.of());
        mockMvc.perform(get("/items/search")
                        .param("text", "test")
                        .header("X-Sharer-User-Id", 12L))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService).findItems("test", 12L);
    }
}
