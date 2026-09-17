package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Интеграционные тесты сервиса пользователей
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    //Создание пользователя
    @Test
    void createShouldSaveUser() {
        UserDto userDto = UserDto.builder()
                .name("Анна")
                .email("anna@test.ru")
                .build();

        UserDto result = userService.create(userDto);

        assertNotNull(result.getId());
        assertEquals("Анна", result.getName());
        assertEquals("anna@test.ru", result.getEmail());

        User savedUser = userRepository
                .findById(result.getId())
                .orElseThrow();

        assertEquals("Анна", savedUser.getName());
        assertEquals("anna@test.ru", savedUser.getEmail());
    }

    //Обновление пользователя
    @Test
    void updateShouldChangeUser() {
        User user = User.builder()
                .name("Иван")
                .email("ivan@test.ru")
                .build();

        User savedUser = userRepository.save(user);

        UserDto updateDto = UserDto.builder()
                .name("Новое имя")
                .build();

        UserDto result =
                userService.update(savedUser.getId(), updateDto);

        assertEquals("Новое имя", result.getName());
        assertEquals("ivan@test.ru", result.getEmail());

        User updatedUser = userRepository
                .findById(savedUser.getId())
                .orElseThrow();

        assertEquals("Новое имя", updatedUser.getName());
        assertEquals("ivan@test.ru", updatedUser.getEmail());
    }

    //Получение пользователя по ID
    @Test
    void getByIdShouldReturnUser() {
        User user = User.builder()
                .name("Мария")
                .email("maria@test.ru")
                .build();

        User savedUser = userRepository.save(user);

        UserDto result =
                userService.getById(savedUser.getId());

        assertEquals(savedUser.getId(), result.getId());
        assertEquals("Мария", result.getName());
        assertEquals("maria@test.ru", result.getEmail());
    }

    //Получение всех пользователей
    @Test
    void getAllShouldReturnUsers() {
        User firstUser = User.builder()
                .name("Олег")
                .email("oleg@test.ru")
                .build();

        User secondUser = User.builder()
                .name("Светлана")
                .email("sveta@test.ru")
                .build();

        userRepository.save(firstUser);
        userRepository.save(secondUser);

        List<UserDto> result = userService.getAll();

        assertEquals(2, result.size());

        assertTrue(result.stream()
                .anyMatch(user ->
                        "Олег".equals(user.getName())));

        assertTrue(result.stream()
                .anyMatch(user ->
                        "Светлана".equals(user.getName())));
    }

    //Удаление пользователя
    @Test
    void deleteShouldRemoveUser() {
        User user = User.builder()
                .name("Алексей")
                .email("alex@test.ru")
                .build();

        User savedUser = userRepository.save(user);

        assertTrue(userRepository.existsById(savedUser.getId()));

        userService.delete(savedUser.getId());

        assertFalse(userRepository.existsById(savedUser.getId()));
    }
}