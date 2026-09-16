package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Краткая информация о вещи, добавленной в ответ на запрос
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestItemDto {

    private Long id;

    private String name;

    private Long ownerId;
}