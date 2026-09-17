package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//Тесты обработчика исключений
public class ExceptionErrorTest {

    private final ExceptionError exceptionError = new ExceptionError();

    //Ошибка валидации входных данных
    @Test
    void handleValidationShouldReturnBadRequest() {
        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        ResponseEntity<Map<String, String>> response =
                exceptionError.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
                "Validation failed",
                response.getBody().get("error")
        );
    }

    //Отсутствует обязательный заголовок
    @Test
    void handleMissingHeaderShouldReturnBadRequest() {
        MissingRequestHeaderException exception =
                mock(MissingRequestHeaderException.class);

        when(exception.getHeaderName())
                .thenReturn("X-Sharer-User-Id");

        ResponseEntity<Map<String, String>> response =
                exceptionError.handleMissingHeader(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
                "Missing required header: X-Sharer-User-Id",
                response.getBody().get("error")
        );
    }

    //Объект не найден
    @Test
    void handleNotFoundShouldReturnNotFound() {
        NotFoundException exception =
                new NotFoundException("User not found");

        ResponseEntity<Map<String, String>> response =
                exceptionError.handleNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(
                "User not found",
                response.getBody().get("error")
        );
    }

    //Ошибка проверки данных
    @Test
    void handleValidationExceptionShouldReturnBadRequest() {
        ValidationException exception =
                new ValidationException("Invalid data");

        ResponseEntity<Map<String, String>> response =
                exceptionError.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
                "Invalid data",
                response.getBody().get("error")
        );
    }

    //Конфликт данных
    @Test
    void handleConflictShouldReturnConflict() {
        ConflictException exception =
                new ConflictException("Email already exists");

        ResponseEntity<Map<String, String>> response =
                exceptionError.handleConflict(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(
                "Email already exists",
                response.getBody().get("error")
        );
    }

    //Запрещённое действие
    @Test
    void handleForbiddenShouldReturnForbidden() {
        ForbiddenException exception =
                new ForbiddenException("Access denied");

        ResponseEntity<Map<String, String>> response =
                exceptionError.handleForbidden(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(
                "Access denied",
                response.getBody().get("error")
        );
    }

    //Неверный тип параметра запроса
    @Test
    void handleTypeMismatchShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException exception =
                mock(MethodArgumentTypeMismatchException.class);

        ResponseEntity<Map<String, String>> response =
                exceptionError.handleTypeMismatch(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
                "Invalid request parameter",
                response.getBody().get("error")
        );
    }

    //Непредвиденная ошибка
    @Test
    void handleExceptionShouldReturnInternalServerError() {
        Exception exception =
                new Exception("Unexpected error");

        ResponseEntity<Map<String, String>> response =
                exceptionError.handleException(exception);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertEquals(
                "Internal server error",
                response.getBody().get("error")
        );
    }
}