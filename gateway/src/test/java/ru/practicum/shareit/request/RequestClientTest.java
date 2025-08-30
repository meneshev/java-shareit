package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RequestClientTest {

    private RestTemplateBuilder builder;
    private RestTemplate restTemplate;
    private RequestClient client;

    @BeforeEach
    void setUp() {
        builder = mock(RestTemplateBuilder.class, RETURNS_SELF);
        restTemplate = mock(RestTemplate.class);
        when(builder.build()).thenReturn(restTemplate);
        client = new RequestClient("http://server", builder);
    }

    @Test
    void getUserRequests_buildsGetWithUserIdHeader() {
        long userId = 1L;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("requests"));

        ResponseEntity<?> response = client.getUserRequests(userId);
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
    void getOtherUsersRequests_buildsGetWithAllPath() {
        long userId = 2L;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("allRequests"));

        client.getOtherUsersRequests(userId);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(
                pathCaptor.capture(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );

        assertThat(pathCaptor.getValue()).isEqualTo("/all");
    }

    @Test
    void getRequest_buildsGetWithRequestIdPath() {
        long userId = 3L;
        long requestId = 30L;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("request"));

        client.getRequest(userId, requestId);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(
                pathCaptor.capture(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );

        assertThat(pathCaptor.getValue()).isEqualTo("/" + requestId);
    }

    @Test
    void createRequest_postsBodyAndSetsHeader() {
        long userId = 4L;
        CreateItemRequestReqDto dto = new CreateItemRequestReqDto();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("created"));

        ResponseEntity<?> response = client.createRequest(userId, dto);
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
        assertThat(headers.getFirst("X-Sharer-User-Id")).isEqualTo(String.valueOf(userId));
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }
}