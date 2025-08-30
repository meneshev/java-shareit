package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CreateItemRequest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateItemRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_WhenAllFieldsValid() {
        CreateItemRequest request = new CreateItemRequest();
        request.setName("test");
        request.setDescription("test description");
        request.setAvailable(true);

        Set<ConstraintViolation<CreateItemRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_WhenNameIsNull() {
        CreateItemRequest request = new CreateItemRequest();
        request.setDescription("test description");
        request.setAvailable(true);

        Set<ConstraintViolation<CreateItemRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(2);
        Set<Class<?>> annotationTypes = violations.stream()
                .map(v -> v.getConstraintDescriptor().getAnnotation().annotationType())
                .collect(Collectors.toSet());
        assertThat(annotationTypes).containsExactlyInAnyOrder(
                jakarta.validation.constraints.NotNull.class,
                jakarta.validation.constraints.NotEmpty.class
        );
    }

    @Test
    void shouldFailValidation_WhenNameIsEmpty() {
        CreateItemRequest request = new CreateItemRequest();
        request.setName("");
        request.setDescription("test description");
        request.setAvailable(true);

        Set<ConstraintViolation<CreateItemRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType())
                .isEqualTo(jakarta.validation.constraints.NotEmpty.class);
    }

    @Test
    void shouldFailValidation_WhenDescriptionIsNull() {
        CreateItemRequest request = new CreateItemRequest();
        request.setName("test");
        request.setAvailable(true);

        Set<ConstraintViolation<CreateItemRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType())
                .isEqualTo(jakarta.validation.constraints.NotNull.class);
    }

    @Test
    void shouldFailValidation_WhenAvailableIsNull() {
        CreateItemRequest request = new CreateItemRequest();
        request.setName("test");
        request.setDescription("test description");

        Set<ConstraintViolation<CreateItemRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType())
                .isEqualTo(jakarta.validation.constraints.NotNull.class);
    }
}