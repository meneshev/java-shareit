package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @SneakyThrows
    @Test
    void getItemTest() {
        Long userId = 1L;
        Long itemId = 1L;
        String responseBody = "{\"id\":1,\"name\":\"test item\",\"description\":\"test description\"}";

        when(itemClient.getItem(userId, itemId)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(get("/items/{item-id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void getUserItemsTest() {
        Long userId = 1L;
        String responseBody = "[{\"id\":1,\"name\":\"test item\",\"description\":\"test description\"}]";

        when(itemClient.getUserItems(userId)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void searchItemTest() {
        Long userId = 1L;
        String searchString = "test";
        String responseBody = "[{\"id\":1,\"name\":\"test item\",\"description\":\"test description\"}]";

        when(itemClient.searchItem(userId, searchString)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", searchString)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void createItemTest() {
        Long userId = 1L;
        CreateItemRequest createItemRequest = new CreateItemRequest();
        createItemRequest.setName("test item");
        createItemRequest.setDescription("test description");
        createItemRequest.setAvailable(true);

        String responseBody = "{\"id\":1,\"name\":\"test item\",\"description\":\"test description\",\"available\":true}";

        when(itemClient.createItem(userId, createItemRequest)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
        verify(itemClient).createItem(userId, createItemRequest);
    }

    @SneakyThrows
    @Test
    void createCommentTest() {
        Long userId = 1L;
        Long itemId = 1L;
        CreateCommentRequest createCommentRequest = new CreateCommentRequest();
        createCommentRequest.setText("test comment");

        String responseBody = "{\"id\":1,\"text\":\"test comment\"," +
                "\"authorName\":\"test author\"}";

        when(itemClient.createComment(userId, itemId, createCommentRequest))
                .thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(post("/items/{item-id}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        Long userId = 1L;
        Long itemId = 1L;
        UpdateItemRequest updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("updated item");

        String responseBody = "{\"id\":1,\"name\":\"updated item\"," +
                "\"description\":\"test description\",\"available\":true}";

        when(itemClient.updateItem(userId, itemId, updateItemRequest)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(patch("/items/{item-id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
    }

    @SneakyThrows
    @Test
    void deleteItemTest() {
        Long userId = 1L;
        Long itemId = 1L;
        String responseBody = "{\"message\":\"Item has been deleted\"}";

        when(itemClient.deleteItem(userId, itemId)).thenReturn(ResponseEntity.ok(responseBody));

        String result = mockMvc.perform(delete("/items/{item-id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(responseBody, result);
        verify(itemClient).deleteItem(userId, itemId);
    }
}