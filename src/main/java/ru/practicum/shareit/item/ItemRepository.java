package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

//Репозиторий для хранения вещей
@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();                  //Хранилище вещей
    private final AtomicLong idGenerator = new AtomicLong(1);     //Генератор ID

    //Сохранение или обновление вещицы
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idGenerator.getAndIncrement());
        }
        items.put(item.getId(), item);
        return item;
    }

    //Поиск вещицы по ID
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    //Получение всех вещей
    public List<Item> findAllByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null &&
                        item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    //Поиск вещей по тексту
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(searchText)) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
    }

    //Обновление вещей
    public Item update(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new RuntimeException("Item not found");
        }
        items.put(item.getId(), item);
        return item;
    }

    //Удаление вещи по ID
    public void deleteById(Long id) {
        items.remove(id);
    }
}
