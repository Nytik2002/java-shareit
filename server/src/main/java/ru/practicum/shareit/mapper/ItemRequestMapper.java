package ru.practicum.shareit.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.ItemRequest;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.dto.ItemRequestItemDto;

import java.util.List;
import java.util.stream.Collectors;

//Маппер для преобразования запроса вещи в DTO
@UtilityClass
public class ItemRequestMapper {

    //Преобразование ItemRequest в ItemRequestDto
    public ItemRequestDto toItemRequestDto(
            ItemRequest itemRequest,
            List<Item> items) {

        List<ItemRequestItemDto> itemDtos = items.stream()
                .map(ItemRequestMapper::toItemRequestItemDto)
                .collect(Collectors.toList());

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemDtos)
                .build();
    }

    //Преобразование вещи в краткий DTO ответа на запрос
    private ItemRequestItemDto toItemRequestItemDto(Item item) {
        return ItemRequestItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}