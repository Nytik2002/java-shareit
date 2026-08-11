package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

//Интерфейс для работы с пользователями
public interface UserService {
    UserDto create(UserDto userDto);            //Создание

    UserDto update(Long id, UserDto userDto);   //Обновление

    UserDto getById(Long id);                   //Получение ID

    List<UserDto> getAll();                     //Получение всех в списке

    void delete(Long id);                       //Удаление
}
