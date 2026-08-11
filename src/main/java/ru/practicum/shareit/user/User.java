package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Модель пользователя
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;        //Уникальный ID
    private String name;    //Имя
    private String email;   //Email
}
