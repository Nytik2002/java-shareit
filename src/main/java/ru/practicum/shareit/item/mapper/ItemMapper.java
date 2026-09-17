package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

//Маппер для преобразования Item и ItemDto
@UtilityClass
public class ItemMapper {

    //Преобразование Item в ItemDto
    public ItemDto toItemDto(Item item) {
        Long ownerId = null;
        Long requestId = null;

        if (item.getOwner() != null) {
            ownerId = item.getOwner().getId();
        }

        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(ownerId)
                .requestId(requestId)
                .build();
    }

    //Преобразование ItemDto в Item
    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}