package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserClientTest {

    private RestTemplateBuilder builder;
    private RestTemplate restTemplate;
    private UserClient client;

    @BeforeEach
    void setUp() {
        builder = mock(RestTemplateBuilder.class, RETURNS_SELF);
        restTemplate = mock(RestTemplate.class);
        when(builder.build()).thenReturn(restTemplate);
        client = new UserClient("http://server", builder);
    }

    @Test
    void getUsers_buildsGetWithoutUserIdHeader() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("users"));

        ResponseEntity<?> response = client.getUsers();
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
        assertThat(headers.containsKey("X-Sharer-User-Id")).isFalse();
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void getUser_buildsGetWithPathWithoutUserIdHeader() {
        long userId = 5L;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("user"));

        client.getUser(userId);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(
                pathCaptor.capture(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );

        assertThat(pathCaptor.getValue()).isEqualTo("/" + userId);
    }

    @Test
    void createUser_postsBodyWithoutUserIdHeader() {
        CreateUserRequest dto = new CreateUserRequest();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("created"));

        ResponseEntity<?> response = client.createUser(dto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                urlCaptor.capture(),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(Object.class)
        );

        assertThat(urlCaptor.getValue()).isEqualTo("");
        HttpEntity<?> entity = entityCaptor.getValue();
        assertThat(entity.getBody()).isEqualTo(dto);
        HttpHeaders headers = entity.getHeaders();
        assertThat(headers.containsKey("X-Sharer-User-Id")).isFalse();
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void updateUser_patchesBodyWithoutUserIdHeader() {
        long userId = 7L;
        UpdateUserRequest dto = new UpdateUserRequest();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("updated"));

        client.updateUser(userId, dto);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                pathCaptor.capture(),
                eq(HttpMethod.PATCH),
                entityCaptor.capture(),
                eq(Object.class)
        );

        assertThat(pathCaptor.getValue()).isEqualTo("/" + userId);
        assertThat(entityCaptor.getValue().getBody()).isEqualTo(dto);
    }

    @Test
    void deleteUser_buildsDeleteWithoutUserIdHeader() {
        long userId = 8L;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<?> response = client.deleteUser(userId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                pathCaptor.capture(),
                eq(HttpMethod.DELETE),
                entityCaptor.capture(),
                eq(Object.class)
        );

        assertThat(pathCaptor.getValue()).isEqualTo("/" + userId);
        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertThat(headers.containsKey("X-Sharer-User-Id")).isFalse();
    }
}