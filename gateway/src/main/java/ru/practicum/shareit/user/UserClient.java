package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

//Клиент для отправки запросов пользователей на сервер
@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplateBuilder builder) {

        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    //Создание нового пользователя
    public ResponseEntity<Object> create(NewUserRequest userRequest) {
        return post("", userRequest);
    }

    //Обновление пользователя
    public ResponseEntity<Object> update(Long userId, UpdateUserRequest userRequest) {
        return patch("/" + userId, userRequest);
    }

    //Получение пользователя по ID
    public ResponseEntity<Object> getById(Long userId) {
        return get("/" + userId);
    }

    //Получение всех пользователей
    public ResponseEntity<Object> getAll() {
        return get("");
    }

    //Удаление пользователя
    public ResponseEntity<Object> deleteById(Long userId) {
        return delete("/" + userId);
    }
}