package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
public class UpdateUserRequestJsonTest {

    @Autowired
    private JacksonTester<UpdateUserRequest> json;

    @Test
    void shouldSerializeUpdateUserRequest() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("test");
        request.setEmail("test@test.com");

        assertThat(json.write(request))
                .hasJsonPathStringValue("$.name")
                .hasJsonPathStringValue("$.email");

        request.setName(null);
        request.setEmail("test@test.com");

        assertThat(json.write(request))
                .doesNotHaveJsonPathValue("$.name")
                .hasJsonPathStringValue("$.email");
    }

    @Test
    void shouldDeserializeUpdateUserRequest() throws Exception {
        String content = "{\"name\": \"test\",\"email\": \"test@test.com\"}";

        UpdateUserRequest request = json.parseObject(content);

        assertThat(request.getName()).isEqualTo("test");
        assertThat(request.getEmail()).isEqualTo("test@test.com");

        content = "{\"email\": \"test@test.com\"}";

        request = json.parseObject(content);

        assertThat(request.getEmail()).isEqualTo("test@test.com");
        assertTrue(Objects.isNull(request.getName()));
    }
}
