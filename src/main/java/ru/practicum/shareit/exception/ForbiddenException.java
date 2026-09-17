package ru.practicum.shareit.exception;

//Исключение для запрещённых действий
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}