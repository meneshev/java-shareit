package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    private BaseClient baseClient;

    private final Long userId = 1L;
    private final String path = "/test";
    private final Map<String, Object> parameters = Map.of("param1", "value1", "param2", "value2");

    @BeforeEach
    void setUp() {
        baseClient = new BaseClient(restTemplate);
    }

    @Test
    void get_PathOnly_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.get(path);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void get_WithUserId_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.get(path, userId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody());

        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), argThat(httpEntity -> {
            HttpHeaders headers = httpEntity.getHeaders();
            return "1".equals(headers.getFirst("X-Sharer-User-Id"));
        }), eq(Object.class));
    }

    @Test
    void get_WithUserIdAndParameters_Success() {

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);


        ResponseEntity<Object> result = baseClient.get(path, userId, parameters);


        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void get_WithNullUserId_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.get(path, (Long) null, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), argThat(httpEntity -> {
            HttpHeaders headers = httpEntity.getHeaders();
            return !headers.containsKey("X-Sharer-User-Id");
        }), eq(Object.class));
    }

    @Test
    void post_WithBody_Success() {

        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).body("created");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.post(path, requestBody);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("created", result.getBody());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.POST), argThat(httpEntity ->
                requestBody.equals(httpEntity.getBody())
        ), eq(Object.class));
    }

    @Test
    void post_WithUserIdAndBody_Success() {
        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).body("created");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.post(path, userId, requestBody);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("created", result.getBody());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.POST), argThat(httpEntity -> {
            HttpHeaders headers = httpEntity.getHeaders();
            return requestBody.equals(httpEntity.getBody()) && "1".equals(headers.getFirst("X-Sharer-User-Id"));
        }), eq(Object.class));
    }

    @Test
    void post_WithAllParameters_Success() {
        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).body("created");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.post(path, userId, parameters, requestBody);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("created", result.getBody());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void put_WithUserIdAndBody_Success() {

        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("updated");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.put(path, userId, requestBody);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("updated", result.getBody());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void put_WithAllParameters_Success() {
        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("updated");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.put(path, userId, parameters, requestBody);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("updated", result.getBody());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void patch_WithBodyOnly_Success() {
        String requestBody = "patch body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("patched");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.patch(path, requestBody);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("patched", result.getBody());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void patch_WithUserIdAndBody_Success() {
        String requestBody = "patch body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("patched");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.patch(path, userId, requestBody);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("patched", result.getBody());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void patch_WithUserIdAndParameters_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("patched");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.patch(path, userId, parameters);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("patched", result.getBody());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void patch_WithAllParameters_Success() {
        String requestBody = "patch body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("patched");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.patch(path, userId, parameters, requestBody);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("patched", result.getBody());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void delete_PathOnly_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();
        when(restTemplate.exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.delete(path);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void delete_WithUserId_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();
        when(restTemplate.exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.delete(path, userId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.DELETE), argThat(httpEntity -> {
            HttpHeaders headers = httpEntity.getHeaders();
            return "1".equals(headers.getFirst("X-Sharer-User-Id"));
        }), eq(Object.class));
    }

    @Test
    void delete_WithAllParameters_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();
        when(restTemplate.exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.delete(path, userId, parameters);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void makeAndSendRequest_HttpClientErrorException_ReturnsErrorResponse() {
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "Error body".getBytes(), null);
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> result = baseClient.get(path);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertArrayEquals("Error body".getBytes(), (byte[]) result.getBody());
    }

    @Test
    void makeAndSendRequest_HttpServerErrorException_ReturnsErrorResponse() {
        HttpServerErrorException exception = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Server error".getBytes(), null);
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> result = baseClient.post(path, "test body");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertArrayEquals("Server error".getBytes(), (byte[]) result.getBody());
    }

    @Test
    void makeAndSendRequest_HttpStatusCodeException_ReturnsErrorResponse() {
        HttpStatusCodeException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found", "Resource not found".getBytes(), null);
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> result = baseClient.get(path, userId);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertArrayEquals("Resource not found".getBytes(), (byte[]) result.getBody());
    }

    @Test
    void defaultHeaders_WithoutUserId_ContainsStandardHeaders() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        baseClient.get(path, (Long) null, null);

        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), argThat(httpEntity -> {
            HttpHeaders headers = httpEntity.getHeaders();
            return MediaType.APPLICATION_JSON.equals(headers.getContentType()) &&
                    headers.getAccept().contains(MediaType.APPLICATION_JSON) &&
                    !headers.containsKey("X-Sharer-User-Id");
        }), eq(Object.class));
    }

    @Test
    void defaultHeaders_WithUserId_ContainsUserIdHeader() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        baseClient.get(path, userId, null);

        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), argThat(httpEntity -> {
            HttpHeaders headers = httpEntity.getHeaders();
            return MediaType.APPLICATION_JSON.equals(headers.getContentType()) &&
                    headers.getAccept().contains(MediaType.APPLICATION_JSON) &&
                    "1".equals(headers.getFirst("X-Sharer-User-Id"));
        }), eq(Object.class));
    }

    @Test
    void prepareGatewayResponse_SuccessfulResponse_ReturnsOriginalResponse() {
        ResponseEntity<Object> successResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(successResponse);

        ResponseEntity<Object> result = baseClient.get(path);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody());
    }

    @Test
    void prepareGatewayResponse_ErrorResponseWithBody_ReturnsErrorResponse() {
        ResponseEntity<Object> errorResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error message");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(errorResponse);

        ResponseEntity<Object> result = baseClient.get(path);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("error message", result.getBody());
    }

    @Test
    void prepareGatewayResponse_ErrorResponseWithoutBody_ReturnsErrorResponseWithoutBody() {

        ResponseEntity<Object> errorResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(errorResponse);

        ResponseEntity<Object> result = baseClient.get(path);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void post_WithNullBody_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.post(path, userId, (String) null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.POST), argThat(httpEntity ->
                httpEntity.getBody() == null
        ), eq(Object.class));
    }

    @Test
    void patch_WithComplexObject_Success() {
        TestDto requestDto = new TestDto("test", 123);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("patched");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.patch(path, userId, parameters, requestDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PATCH), argThat(httpEntity ->
                requestDto.equals(httpEntity.getBody())
        ), eq(Object.class), eq(parameters));
    }

    @Test
    void get_WithEmptyParameters_Success() {
        Map<String, Object> emptyParams = Map.of();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(emptyParams)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.get(path, userId, emptyParams);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(emptyParams));
    }

    static class TestDto {
        private String name;
        private Integer value;

        public TestDto() {

        }

        public TestDto(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestDto testDto = (TestDto) obj;
            return name.equals(testDto.name) && value.equals(testDto.value);
        }
    }
}