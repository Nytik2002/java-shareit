package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//DTO данных о вещи
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;                //Уникальный ID
    private String description;     //Описание желаемой вещи
    private Long requestorId;       //ID пользователя создавшего запрос
    private LocalDateTime created;  //Дата/время создания запроса
}
