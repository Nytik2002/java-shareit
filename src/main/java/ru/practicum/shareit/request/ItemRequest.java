package ru.practicum.shareit.request;

import ru.practicum.shareit.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//Запрос на создание вещи
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private Long id;                //Уникальный ID
    private String description;     //Описание желаемой вещи
    private User requestor;         //Пользователя создавший запрос
    private LocalDateTime created;  //Дата/время когда было создан запроса
}
