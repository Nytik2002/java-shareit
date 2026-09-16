package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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
}