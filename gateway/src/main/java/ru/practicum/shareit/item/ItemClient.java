package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Map;

//Клиент для отправки запросов вещей на сервер
@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplateBuilder builder) {

        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    //Создание новой вещи
    public ResponseEntity<Object> create(Long userId, NewItemRequest itemRequest) {
        return post("", userId, itemRequest);
    }

    //Обновление вещи
    public ResponseEntity<Object> update(
            Long userId,
            Long itemId,
            UpdateItemRequest itemRequest) {

        return patch("/" + itemId, userId, itemRequest);
    }

    //Получение вещи по ID
    public ResponseEntity<Object> getById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    //Получение всех вещей владельца
    public ResponseEntity<Object> getAllByOwner(Long userId) {
        return get("", userId);
    }

    //Поиск вещей
    public ResponseEntity<Object> search(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", null, parameters);
    }

    //Добавление комментария
    public ResponseEntity<Object> addComment(
            Long userId,
            Long itemId,
            NewCommentRequest commentRequest) {

        return post("/" + itemId + "/comment", userId, commentRequest);
    }
}