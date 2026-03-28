package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CreateCommentRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateCommentRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_WhenAllFieldsValid() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("test comment");

        Set<ConstraintViolation<CreateCommentRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_WhenTextIsNull() {
        CreateCommentRequest request = new CreateCommentRequest();

        Set<ConstraintViolation<CreateCommentRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType())
                .isEqualTo(jakarta.validation.constraints.NotNull.class);
    }
}