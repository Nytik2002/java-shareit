package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

//Обработчик ошибок
@RestControllerAdvice
public class ExceptionError {

    //Обработка ошибок входных данных
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Validation failed"));
    }

    //Обработка отсутствия заголовка
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, String>> handleMissingHeader(MissingRequestHeaderException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Missing required header: " + e.getHeaderName()));
    }

    //Обработка случая, когда объект не найден
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }

    //Обработка ошибок проверки данных
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    //Обработка конфликтов данных
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflict(ConflictException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
    }

    //Обработка запрещенных действий
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", e.getMessage()));
    }

    //Обработка неправильного значения параметра запроса
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid request parameter"));
    }

    //Обработка ошибок
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
    }
}