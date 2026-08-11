package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;                //Уникальный ID
    private LocalDateTime start;    //Дата/время начала бронирования
    private LocalDateTime end;      //Дата/время окончания бронирования
    private Long itemId;            //ID вещицы, которую бронирую
    private Long bookerId;          //ID пользователя, который бронируется
    private String status;          //Статус бронирования
}
