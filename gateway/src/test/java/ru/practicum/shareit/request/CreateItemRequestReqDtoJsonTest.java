package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CreateItemRequestReqDtoJsonTest {

    @Autowired
    private JacksonTester<CreateItemRequestReqDto> json;

    @Test
    void shouldSerializeCreateItemRequest_RequestDto() throws Exception {
        CreateItemRequestReqDto request = new CreateItemRequestReqDto();
        request.setDescription("test description");

        assertThat(json.write(request))
                .hasJsonPathStringValue("$.description");
    }

    @Test
    void shouldDeserializeCreateItemRequest_RequestDto() throws Exception {
        String content = "{\"description\": \"test description\"}";

        CreateItemRequestReqDto request = json.parseObject(content);

        assertThat(request.getDescription()).isEqualTo("test description");
    }
}