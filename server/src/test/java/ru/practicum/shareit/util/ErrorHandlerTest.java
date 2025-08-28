package ru.practicum.shareit.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleNotFound_ShouldReturnNotFoundErrorResponse() {
        NotFoundException exception = new NotFoundException("Детальная информация об ошибке");
        ErrorResponse response = errorHandler.handleNotFound(exception);

        assertEquals("Детальная информация об ошибке", response.getError());
        assertEquals("Объект не найден", response.getMessage());
    }

    @Test
    void handleValidation_ShouldReturnValidationErrorResponse() {
        ValidationException exception = new ValidationException("Неправильные данные");
        ErrorResponse response = errorHandler.handleValidation(exception);

        assertEquals("Неправильные данные", response.getError());
        assertEquals("Ошибка валидации", response.getMessage());
    }

    @Test
    void handleCommonException_RuntimeException_ShouldReturnInternalServerErrorResponse() {
        RuntimeException exception = new RuntimeException("Что-то пошло не так");
        ErrorResponse response = errorHandler.handleCommonException(exception);

        assertEquals("Что-то пошло не так", response.getError());
        assertEquals("Произошла неожиданная ошибка", response.getMessage());
    }

    @Test
    void handleCommonException_BindException_ShouldReturnValidationErrorResponse() {
        BindException exception = new BindException(this, "objectName");
        exception.reject("field", "Неверное значение");
        ErrorResponse response = errorHandler.handleCommonException(exception);

        String s = response.getError();

        String message = response.getMessage();

        assertEquals("Ошибка валидации", response.getMessage());
        assertTrue(response.getError().contains("objectName"));
    }
}
