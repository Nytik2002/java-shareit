package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;                //Уникальный ID
    private String name;            //Имя вещицы
    private String description;     //Описание вещицы
    private Boolean available;      //Доступность вещи для бронирования
    private Long ownerId;           //ID владельца этой вещицы
    private Long requestId;         //ID запроса на вещь
}
