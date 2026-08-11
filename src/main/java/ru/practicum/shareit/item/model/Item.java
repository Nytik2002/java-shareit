package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Long id;                //Уникальный ID
    private String name;            //Имя вещицы
    private String description;     //Описание вещицы
    private Boolean available;      //Доступность бронирования
    private User owner;             //Владелец вещи
    private ItemRequest request;    //Запрос
}
