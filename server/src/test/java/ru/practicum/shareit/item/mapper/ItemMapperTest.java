package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Тесты маппера вещей
public class ItemMapperTest {

    //Преобразование вещи с владельцем и запросо в DTO
    @Test
    void toItemDtoShouldMapOwnerAndRequest() {
        User owner = User.builder()
                .id(10L)
                .name("Анна")
                .email("anna@test.ru")
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(20L)
                .description("Нужна дрель")
                .build();

        Item item = Item.builder()
                .id(30L)
                .name("Дрель")
                .description("Обычная дрель")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        ItemDto result = ItemMapper.toItemDto(item);

        assertEquals(30L, result.getId());
        assertEquals("Дрель", result.getName());
        assertEquals("Обычная дрель", result.getDescription());
        assertEquals(true, result.getAvailable());
        assertEquals(10L, result.getOwnerId());
        assertEquals(20L, result.getRequestId());
    }
}