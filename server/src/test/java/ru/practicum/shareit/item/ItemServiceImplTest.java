package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Интеграционные тесты сервиса вещей
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    //Создание вещи
    @Test
    void createShouldSaveItem() {
        User owner = User.builder()
                .name("Анна")
                .email("anna-item@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);

        ItemDto itemDto = ItemDto.builder()
                .name("Дрель")
                .description("Обычная дрель")
                .available(true)
                .build();

        ItemDto result =
                itemService.create(savedOwner.getId(), itemDto);

        assertNotNull(result.getId());
        assertEquals("Дрель", result.getName());
        assertEquals("Обычная дрель", result.getDescription());
        assertTrue(result.getAvailable());

        Item savedItem = itemRepository
                .findById(result.getId())
                .orElseThrow();

        assertEquals("Дрель", savedItem.getName());
        assertEquals(savedOwner.getId(), savedItem.getOwner().getId());
        assertTrue(savedItem.getAvailable());
    }

    //Обновление вещи
    @Test
    void updateShouldChangeItem() {
        User owner = User.builder()
                .name("Иван")
                .email("ivan-item@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);

        Item item = Item.builder()
                .name("Старая дрель")
                .description("Старое описание")
                .available(true)
                .owner(savedOwner)
                .build();

        Item savedItem = itemRepository.save(item);

        ItemDto updateDto = ItemDto.builder()
                .name("Новая дрель")
                .available(false)
                .build();

        ItemDto result = itemService.update(
                savedOwner.getId(),
                savedItem.getId(),
                updateDto
        );

        assertEquals("Новая дрель", result.getName());
        assertEquals("Старое описание", result.getDescription());
        assertFalse(result.getAvailable());

        Item updatedItem = itemRepository
                .findById(savedItem.getId())
                .orElseThrow();

        assertEquals("Новая дрель", updatedItem.getName());
        assertEquals("Старое описание", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    //Получение вещи по ID
    @Test
    void getByIdShouldReturnItem() {
        User owner = User.builder()
                .name("Мария")
                .email("maria-item@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);

        Item item = Item.builder()
                .name("Перфоратор")
                .description("Мощный перфоратор")
                .available(true)
                .owner(savedOwner)
                .build();

        Item savedItem = itemRepository.save(item);

        ItemDto result = itemService.getById(
                savedOwner.getId(),
                savedItem.getId()
        );

        assertEquals(savedItem.getId(), result.getId());
        assertEquals("Перфоратор", result.getName());
        assertEquals("Мощный перфоратор", result.getDescription());
        assertTrue(result.getAvailable());
    }

    //Получение всех вещей владельца
    @Test
    void getAllByOwnerShouldReturnOnlyOwnersItems() {
        User owner = User.builder()
                .name("Олег")
                .email("oleg-item@test.ru")
                .build();

        User otherOwner = User.builder()
                .name("Сергей")
                .email("sergey-item@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);
        User savedOtherOwner = userRepository.save(otherOwner);

        Item firstItem = Item.builder()
                .name("Дрель")
                .description("Первая вещь")
                .available(true)
                .owner(savedOwner)
                .build();

        Item secondItem = Item.builder()
                .name("Молоток")
                .description("Вторая вещь")
                .available(true)
                .owner(savedOwner)
                .build();

        Item otherItem = Item.builder()
                .name("Отвёртка")
                .description("Чужая вещь")
                .available(true)
                .owner(savedOtherOwner)
                .build();

        itemRepository.save(firstItem);
        itemRepository.save(secondItem);
        itemRepository.save(otherItem);

        List<ItemDto> result =
                itemService.getAllByOwner(savedOwner.getId());

        assertEquals(2, result.size());

        assertTrue(result.stream()
                .anyMatch(item -> "Дрель".equals(item.getName())));

        assertTrue(result.stream()
                .anyMatch(item -> "Молоток".equals(item.getName())));

        assertFalse(result.stream()
                .anyMatch(item -> "Отвёртка".equals(item.getName())));
    }

    //Поиск доступных вещей
    @Test
    void searchShouldReturnOnlyAvailableMatchingItems() {
        User owner = User.builder()
                .name("Алексей")
                .email("alex-item@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);

        Item availableItem = Item.builder()
                .name("Дрель")
                .description("Аккумуляторная")
                .available(true)
                .owner(savedOwner)
                .build();

        Item unavailableItem = Item.builder()
                .name("Большая дрель")
                .description("Профессиональная")
                .available(false)
                .owner(savedOwner)
                .build();

        Item otherItem = Item.builder()
                .name("Молоток")
                .description("Обычный молоток")
                .available(true)
                .owner(savedOwner)
                .build();

        itemRepository.save(availableItem);
        itemRepository.save(unavailableItem);
        itemRepository.save(otherItem);

        List<ItemDto> result = itemService.search("дрель");

        assertEquals(1, result.size());
        assertEquals("Дрель", result.get(0).getName());
        assertTrue(result.get(0).getAvailable());
    }
}