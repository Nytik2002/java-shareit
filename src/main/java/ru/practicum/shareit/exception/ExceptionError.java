package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    //Обработка всех RuntimeException и определение статуса по тексту сообщения
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException e) {
        String message = e.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (message != null) {
            if (message.contains("not found")) {
                status = HttpStatus.NOT_FOUND;
            } else if (message.contains("Email already exists")) {
                status = HttpStatus.CONFLICT;
            } else if (message.contains("is required")) {
                status = HttpStatus.BAD_REQUEST;
            } else if (message.contains("Only owner")) {
                status = HttpStatus.FORBIDDEN;
            }
        }

        return ResponseEntity
                .status(status)
                .body(Map.of("error", message != null ? message : "Internal server error"));
    }
}
