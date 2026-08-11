package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private Long id;                //Уникальный ID
    private LocalDateTime start;    //Дата/время начала бронирования
    private LocalDateTime end;      //Дата/время окончания бронирования
    private Item item;              //Вещица, которую бронирую
    private User booker;            //Объект пользователя, который бронирует
    private BookingStatus status;   //enum статус бронирования
}
