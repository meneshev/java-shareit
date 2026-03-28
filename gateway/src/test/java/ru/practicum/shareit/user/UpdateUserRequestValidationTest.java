package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateUserRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_WhenAllFieldsValid() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("test");
        request.setEmail("test@test.com");

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_WhenEmailIsIncorrect() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("test");
        request.setEmail("test");

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType())
                .isEqualTo(jakarta.validation.constraints.Email.class);
    }
}
