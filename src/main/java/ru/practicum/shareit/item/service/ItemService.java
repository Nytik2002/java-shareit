package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);                   //Создание новой вещи

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);      //Обновление вещи

    ItemDto getById(Long itemId);                                   //Получение вещи по ID

    List<ItemDto> getAllByOwner(Long userId);                       //Получение списка владельцев вещей

    List<ItemDto> search(String text);                              //Поиск вещей по тексту
}