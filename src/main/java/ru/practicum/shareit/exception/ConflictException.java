package ru.practicum.shareit.exception;

//Исключение для конфликтов данных
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}