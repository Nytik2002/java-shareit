package ru.practicum.shareit.exception;

//Ответ с описанием ошибки
public class ErrorResponse {

    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}