package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookingClientTest {

    RestTemplateBuilder builder;
    RestTemplate restTemplate;
    BookingClient client;

    @BeforeEach
    void setUp() {
        builder = mock(RestTemplateBuilder.class, RETURNS_SELF);
        restTemplate = mock(RestTemplate.class);
        when(builder.build()).thenReturn(restTemplate);

        client  = new BookingClient("http://server", builder);
    }


    @Test
    void getBooking_buildsGet_withUserIdHeader() {
        long userId = 10L;
        long bookingId = 55L;

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(Map.of("ok", true)));

        var response = client.getBooking(userId, bookingId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<?>> entity = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(url.capture(), eq(HttpMethod.GET), entity.capture(), eq(Object.class));
        assertThat(url.getValue()).isEqualTo("/" + bookingId);

        HttpHeaders headers = entity.getValue().getHeaders();
        assertThat(headers.getFirst("X-Sharer-User-Id")).isEqualTo(String.valueOf(userId));
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void getBookings_asRequester_buildsQuery() {
        long userId = 7L;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok("ok"));

        var resp = client.getBookings(userId, BookingState.ALL, 0, 10, false);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> params = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate).exchange(path.capture(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), params.capture());

        assertThat(path.getValue()).isEqualTo("?state={state}&from={from}&size={size}");
        assertThat(params.getValue()).containsEntry("state", "ALL").containsEntry("from", 0).containsEntry("size", 10);
    }

    @Test
    void getBookings_asOwner_buildsOwnerPath() {
        long userId = 7L;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok("ok"));

        client.getBookings(userId, BookingState.REJECTED, 5, 20, true);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> params = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate).exchange(path.capture(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), params.capture());

        assertThat(path.getValue()).isEqualTo("/owner?state={state}&from={from}&size={size}");
        assertThat(params.getValue()).containsEntry("state", "REJECTED").containsEntry("from", 5).containsEntry("size", 20);
    }

    @Test
    void bookItem_postsBody_andSetsHeader() {
        long userId = 3L;
        var dto = new CreateBookingRequest();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("created"));

        var resp = client.bookItem(userId, dto);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ArgumentCaptor<HttpEntity<?>> entity = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(""), eq(HttpMethod.POST), entity.capture(), eq(Object.class));
        assertThat(entity.getValue().getBody()).isEqualTo(dto);

        HttpHeaders headers = entity.getValue().getHeaders();
        assertThat(headers.getFirst("X-Sharer-User-Id")).isEqualTo(String.valueOf(userId));
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void approveBooking_patchesWithParam() {
        long userId = 9L;
        long bookingId = 42L;

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok("ok"));

        var resp = client.approveBooking(userId, bookingId, true);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> params = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate).exchange(path.capture(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), params.capture());

        assertThat(path.getValue()).isEqualTo("/" + bookingId + "?approved={approved}");
        assertThat(params.getValue()).containsEntry("approved", true);
    }
}
