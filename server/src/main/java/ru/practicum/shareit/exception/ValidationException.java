package ru.practicum.shareit.exception;

//Исключение для ошибок входных данных
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}