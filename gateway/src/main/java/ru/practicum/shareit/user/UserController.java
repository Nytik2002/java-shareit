package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

//Контроллер HTTP-запросов пользователей
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    //Создание нового пользователя
    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody NewUserRequest userRequest) {

        return userClient.create(userRequest);
    }

    //Обновление пользователя
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest userRequest) {

        return userClient.update(userId, userRequest);
    }

    //Получение пользователя по ID
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable Long userId) {
        return userClient.getById(userId);
    }

    //Получение всех пользователей
    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    //Удаление пользователя
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        return userClient.deleteById(userId);
    }
}