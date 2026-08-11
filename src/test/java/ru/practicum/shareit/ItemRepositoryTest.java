package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRepositoryTest {

    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository = new ItemRepository();
    }

    private User createOwner(Long id, String name) {
        return User.builder()
                .id(id)
                .name(name)
                .email(name.toLowerCase() + "@example.com")
                .build();
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = Item.builder()
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .build();
        return itemRepository.save(item);
    }

    @Test
    @DisplayName("Создание вещи")
    void save_ShouldReturnItemWithId() {
        User owner = createOwner(1L, "John");

        Item result = createItem("Drill", "Powerful drill", true, owner);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Drill");
    }

    @Test
    @DisplayName("Поиск вещи по ID")
    void findById_ShouldReturnItem() {
        User owner = createOwner(1L, "John");
        Item saved = createItem("Drill", "Powerful drill", true, owner);

        Optional<Item> result = itemRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Drill");
    }

    @Test
    @DisplayName("Поиск по несуществующему ID")
    void findById_NotFound_ShouldReturnEmpty() {
        Optional<Item> result = itemRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Получение вещей владельца")
    void findAllByOwnerId_ShouldReturnOwnersItems() {
        User owner = createOwner(1L, "John");
        createItem("Drill", "Powerful drill", true, owner);
        createItem("Hammer", "Big hammer", true, owner);

        List<Item> result = itemRepository.findAllByOwnerId(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Поиск вещей по тексту")
    void search_ShouldFindMatchingItems() {
        User owner = createOwner(1L, "John");
        createItem("Drill", "Powerful drill", true, owner);
        createItem("Hammer", "Big hammer", true, owner);

        List<Item> result = itemRepository.search("drill");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Drill");
    }
}
