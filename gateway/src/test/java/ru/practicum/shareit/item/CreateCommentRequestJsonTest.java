package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CreateCommentRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CreateCommentRequestJsonTest {

    @Autowired
    private JacksonTester<CreateCommentRequest> json;

    @Test
    void shouldSerializeCreateCommentRequest() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("test comment");

        assertThat(json.write(request))
                .hasJsonPathStringValue("$.text");
    }

    @Test
    void shouldDeserializeCreateCommentRequest() throws Exception {
        String content = "{\"text\": \"test comment\"}";

        CreateCommentRequest request = json.parseObject(content);

        assertThat(request.getText()).isEqualTo("test comment");
    }
}