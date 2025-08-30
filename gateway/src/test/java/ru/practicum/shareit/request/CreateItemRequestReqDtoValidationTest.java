package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateItemRequestReqDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_WhenAllFieldsValid() {
        CreateItemRequestReqDto request = new CreateItemRequestReqDto();
        request.setDescription("test description");

        Set<ConstraintViolation<CreateItemRequestReqDto>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_WhenDescriptionIsNull() {
        CreateItemRequestReqDto request = new CreateItemRequestReqDto();

        Set<ConstraintViolation<CreateItemRequestReqDto>> violations = validator.validate(request);


        assertThat(violations).hasSize(2);

        Set<Class<?>> annotationTypes = violations.stream()
                .map(v -> v.getConstraintDescriptor()
                        .getAnnotation().annotationType())
                .collect(Collectors.toSet());

        assertThat(annotationTypes)
                .containsExactlyInAnyOrder(
                        jakarta.validation.constraints.NotNull.class,
                        jakarta.validation.constraints.NotEmpty.class
                );
    }

    @Test
    void shouldFailValidation_WhenDescriptionIsEmpty() {
        CreateItemRequestReqDto request = new CreateItemRequestReqDto();
        request.setDescription("");

        Set<ConstraintViolation<CreateItemRequestReqDto>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType())
                .isEqualTo(jakarta.validation.constraints.NotEmpty.class);
    }
}
