package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.CreateUserRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CreateUserRequestJsonTest {

    @Autowired
    private JacksonTester<CreateUserRequest> json;

    @Test
    void shouldSerializeCreateUserRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("test");
        request.setEmail("test@test.com");

        assertThat(json.write(request))
                .hasJsonPathStringValue("$.name")
                .hasJsonPathStringValue("$.email");
    }

    @Test
    void shouldDeserializeCreateUserRequest() throws Exception {
        String content = "{\"name\": \"test\",\"email\": \"test@test.com\"}";

        CreateUserRequest request = json.parseObject(content);

        assertThat(request.getName()).isEqualTo("test");
        assertThat(request.getEmail()).isEqualTo("test@test.com");
    }
}
