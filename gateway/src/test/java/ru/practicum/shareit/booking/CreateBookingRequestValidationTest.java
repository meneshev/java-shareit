package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateBookingRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_WhenAllFieldsValid() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_WhenItemIdIsNull() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType())
                .isEqualTo(jakarta.validation.constraints.NotNull.class);
    }

    @Test
    void shouldFailValidation_WhenStartIsNull() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(1L);
        request.setEnd(LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType())
                .isEqualTo(jakarta.validation.constraints.NotNull.class);
    }

    @Test
    void shouldFailValidation_WhenEndIsNull() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType())
                .isEqualTo(jakarta.validation.constraints.NotNull.class);
    }

    @Test
    void shouldFailValidation_WhenStartIsInPast() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().minusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType())
                .isEqualTo(jakarta.validation.constraints.FutureOrPresent.class);
    }

    @Test
    void shouldFailValidation_WhenEndIsInPast() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().minusDays(1));

        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType())
                .isEqualTo(jakarta.validation.constraints.FutureOrPresent.class);
    }

    @Test
    void shouldFailValidation_WhenBothStartAndEndAreNull() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setItemId(1L);

        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(2);
        Set<Class<?>> annotationTypes = violations.stream()
                .map(v -> v.getConstraintDescriptor().getAnnotation().annotationType())
                .collect(Collectors.toSet());
        assertThat(annotationTypes).containsExactlyInAnyOrder(
                jakarta.validation.constraints.NotNull.class
        );
    }
}