package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CreateBookingRequestJsonTest {

    @Autowired
    private JacksonTester<CreateBookingRequest> json;

    @Test
    void shouldSerializeCreateBookingRequest() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.of(2026, 1, 1, 10, 0));
        request.setEnd(LocalDateTime.of(2026, 1, 1, 12, 0));

        assertThat(json.write(request))
                .hasJsonPathNumberValue("$.itemId")
                .hasJsonPathStringValue("$.start")
                .hasJsonPathStringValue("$.end");
    }

    @Test
    void shouldDeserializeCreateBookingRequest() throws Exception {
        String content = "{\"itemId\": 1,\"start\": \"2026-01-01T10:00:00\",\"end\": \"2026-01-01T12:00:00\"}";

        CreateBookingRequest request = json.parseObject(content);

        assertThat(request.getItemId()).isEqualTo(1L);
        assertThat(request.getStart()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0));
        assertThat(request.getEnd()).isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0));
    }
}