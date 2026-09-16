package ru.practicum.shareit.exception;

//Исключение для случаев, когда объект не найден
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}