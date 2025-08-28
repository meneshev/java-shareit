package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ItemClientTest {

    private RestTemplateBuilder builder;
    private RestTemplate restTemplate;
    private ItemClient client;

    @BeforeEach
    void setUp() {
        builder = mock(RestTemplateBuilder.class, RETURNS_SELF);
        restTemplate = mock(RestTemplate.class);
        when(builder.build()).thenReturn(restTemplate);

        client = new ItemClient("http://server", builder);
    }

    @Test
    void getUserItems_buildsGetWithUserIdHeader() {
        long userId = 1L;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("items"));

        ResponseEntity<?> response = client.getUserItems(userId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(
                urlCaptor.capture(),
                eq(HttpMethod.GET),
                entityCaptor.capture(),
                eq(Object.class)
        );

        assertThat(urlCaptor.getValue()).isEqualTo("");
        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertThat(headers.getFirst("X-Sharer-User-Id")).isEqualTo(String.valueOf(userId));
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void getItem_buildsGetWithPathAndHeader() {
        long userId = 2L;
        long itemId = 20L;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("item"));

        client.getItem(userId, itemId);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(
                urlCaptor.capture(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );

        assertThat(urlCaptor.getValue()).isEqualTo("/" + itemId);
    }

    @Test
    void searchItem_buildsGetWithQueryParam() {
        long userId = 3L;
        String text = "searchText";
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok("result"));

        client.searchItem(userId, text);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate).exchange(
                pathCaptor.capture(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                paramsCaptor.capture()
        );

        assertThat(pathCaptor.getValue()).isEqualTo("/search?text={searchString}");
        assertThat(paramsCaptor.getValue()).containsEntry("searchString", text);
    }

    @Test
    void createItem_postsBodyAndSetsHeader() {
        long userId = 4L;
        CreateItemRequest dto = new CreateItemRequest();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("created"));

        ResponseEntity<?> response = client.createItem(userId, dto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> entity = entityCaptor.getValue();
        assertThat(entity.getBody()).isEqualTo(dto);
        HttpHeaders headers = entity.getHeaders();
        assertThat(headers.getFirst("X-Sharer-User-Id")).isEqualTo(String.valueOf(userId));
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void createComment_postsBodyWithCorrectPathAndHeader() {
        long userId = 5L;
        long itemId = 50L;
        CreateCommentRequest dto = new CreateCommentRequest();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("comment"));

        client.createComment(userId, itemId, dto);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                pathCaptor.capture(),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(Object.class)
        );

        assertThat(pathCaptor.getValue()).isEqualTo("/" + itemId + "/comment");
        assertThat(entityCaptor.getValue().getBody()).isEqualTo(dto);
    }

    @Test
    void updateItem_patchesBodyWithCorrectPath() {
        long userId = 6L;
        long itemId = 60L;
        UpdateItemRequest dto = new UpdateItemRequest();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("updated"));

        client.updateItem(userId, itemId, dto);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                pathCaptor.capture(),
                eq(HttpMethod.PATCH),
                entityCaptor.capture(),
                eq(Object.class)
        );

        assertThat(pathCaptor.getValue()).isEqualTo("/" + itemId);
        assertThat(entityCaptor.getValue().getBody()).isEqualTo(dto);
    }

    @Test
    void deleteItem_buildsDeleteWithCorrectPathAndHeader() {
        long userId = 7L;
        long itemId = 70L;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<?> response = client.deleteItem(userId, itemId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                pathCaptor.capture(),
                eq(HttpMethod.DELETE),
                entityCaptor.capture(),
                eq(Object.class)
        );

        assertThat(pathCaptor.getValue()).isEqualTo("/" + itemId);
        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertThat(headers.getFirst("X-Sharer-User-Id")).isEqualTo(String.valueOf(userId));
    }
}
