package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CreateItemRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CreateItemRequestJsonTest {

    @Autowired
    private JacksonTester<CreateItemRequest> json;

    @Test
    void shouldSerializeCreateItemRequest() throws Exception {
        CreateItemRequest request = new CreateItemRequest();
        request.setName("test");
        request.setDescription("test description");
        request.setAvailable(true);
        request.setRequestId(1L);

        assertThat(json.write(request))
                .hasJsonPathStringValue("$.name")
                .hasJsonPathStringValue("$.description")
                .hasJsonPathBooleanValue("$.available")
                .hasJsonPathNumberValue("$.requestId");
    }

    @Test
    void shouldDeserializeCreateItemRequest() throws Exception {
        String content = " {\"name\": \"test\",\"description\": \"test description\",\"available\": true,\"requestId\": 1}";

        CreateItemRequest request = json.parseObject(content);

        assertThat(request.getName()).isEqualTo("test");
        assertThat(request.getDescription()).isEqualTo("test description");
        assertThat(request.getAvailable()).isTrue();
        assertThat(request.getRequestId()).isEqualTo(1L);
    }
}