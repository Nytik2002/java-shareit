package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

//Сервис для работы с запросами вещей
public interface ItemRequestService {

    //Создание нового запроса вещи
    ItemRequestDto create(Long userId, ItemRequestDto requestDto);

    //Получение запросов пользователя
    List<ItemRequestDto> getOwnRequests(Long userId);
}