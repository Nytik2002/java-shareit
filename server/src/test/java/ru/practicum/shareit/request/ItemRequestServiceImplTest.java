package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Интеграционные тесты сервиса запросов вещей
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    //Создание запроса вещи
    @Test
    void createShouldSaveItemRequest() {
        User user = User.builder()
                .name("Анна")
                .email("anna@test.ru")
                .build();

        User savedUser = userRepository.save(user);

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Нужна дрель")
                .build();

        ItemRequestDto result =
                itemRequestService.create(savedUser.getId(), requestDto);

        assertNotNull(result.getId());
        assertEquals("Нужна дрель", result.getDescription());
        assertNotNull(result.getCreated());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());

        ItemRequest savedRequest = itemRequestRepository
                .findById(result.getId())
                .orElseThrow();

        assertEquals("Нужна дрель", savedRequest.getDescription());
        assertEquals(savedUser.getId(), savedRequest.getRequestor().getId());
        assertNotNull(savedRequest.getCreated());
    }

    //Получение своих запросов от новых к старым
    @Test
    void getOwnRequestsShouldReturnRequestsFromNewestToOldest() {
        User user = User.builder()
                .name("Иван")
                .email("ivan@test.ru")
                .build();

        User savedUser = userRepository.save(user);

        ItemRequest oldRequest = ItemRequest.builder()
                .description("Старый запрос")
                .requestor(savedUser)
                .created(LocalDateTime.now().minusHours(2))
                .build();

        ItemRequest newRequest = ItemRequest.builder()
                .description("Новый запрос")
                .requestor(savedUser)
                .created(LocalDateTime.now().minusHours(1))
                .build();

        itemRequestRepository.save(oldRequest);
        itemRequestRepository.save(newRequest);

        List<ItemRequestDto> result =
                itemRequestService.getOwnRequests(savedUser.getId());

        assertEquals(2, result.size());
        assertEquals("Новый запрос", result.get(0).getDescription());
        assertEquals("Старый запрос", result.get(1).getDescription());
        assertNotNull(result.get(0).getItems());
        assertNotNull(result.get(1).getItems());
    }

    //Получение запросов других пользователей
    @Test
    void getAllRequestsShouldReturnOnlyOtherUsersRequests() {
        User currentUser = User.builder()
                .name("Пётр")
                .email("petr@test.ru")
                .build();

        User otherUser = User.builder()
                .name("Мария")
                .email("maria@test.ru")
                .build();

        User savedCurrentUser = userRepository.save(currentUser);
        User savedOtherUser = userRepository.save(otherUser);

        ItemRequest ownRequest = ItemRequest.builder()
                .description("Мой запрос")
                .requestor(savedCurrentUser)
                .created(LocalDateTime.now().minusHours(1))
                .build();

        ItemRequest otherOldRequest = ItemRequest.builder()
                .description("Старый чужой запрос")
                .requestor(savedOtherUser)
                .created(LocalDateTime.now().minusHours(3))
                .build();

        ItemRequest otherNewRequest = ItemRequest.builder()
                .description("Новый чужой запрос")
                .requestor(savedOtherUser)
                .created(LocalDateTime.now().minusHours(2))
                .build();

        itemRequestRepository.save(ownRequest);
        itemRequestRepository.save(otherOldRequest);
        itemRequestRepository.save(otherNewRequest);

        List<ItemRequestDto> result =
                itemRequestService.getAllRequests(savedCurrentUser.getId());

        assertEquals(2, result.size());
        assertEquals("Новый чужой запрос", result.get(0).getDescription());
        assertEquals("Старый чужой запрос", result.get(1).getDescription());

        assertTrue(result.stream()
                .noneMatch(request ->
                        "Мой запрос".equals(request.getDescription())));
    }

    //Получение одного запроса вместе с вещью/ответом
    @Test
    void getByIdShouldReturnRequestWithItem() {
        User requestor = User.builder()
                .name("Ольга")
                .email("olga@test.ru")
                .build();

        User owner = User.builder()
                .name("Сергей")
                .email("sergey@test.ru")
                .build();

        User savedRequestor = userRepository.save(requestor);
        User savedOwner = userRepository.save(owner);

        ItemRequest request = ItemRequest.builder()
                .description("Нужен перфоратор")
                .requestor(savedRequestor)
                .created(LocalDateTime.now())
                .build();

        ItemRequest savedRequest = itemRequestRepository.save(request);

        Item item = Item.builder()
                .name("Перфоратор")
                .description("Мощный перфоратор")
                .available(true)
                .owner(savedOwner)
                .request(savedRequest)
                .build();

        Item savedItem = itemRepository.save(item);

        ItemRequestDto result =
                itemRequestService.getById(
                        savedOwner.getId(),
                        savedRequest.getId()
                );

        assertEquals(savedRequest.getId(), result.getId());
        assertEquals("Нужен перфоратор", result.getDescription());
        assertNotNull(result.getCreated());

        assertEquals(1, result.getItems().size());
        assertEquals(savedItem.getId(), result.getItems().get(0).getId());
        assertEquals("Перфоратор", result.getItems().get(0).getName());
        assertEquals(savedOwner.getId(), result.getItems().get(0).getOwnerId());
    }
}