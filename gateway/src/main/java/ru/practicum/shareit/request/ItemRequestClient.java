package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

//Клиент для отправки запросов вещей на сервер
@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplateBuilder builder) {

        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    //Создание нового запроса вещи
    public ResponseEntity<Object> create(
            Long userId,
            ItemRequestDto requestDto) {

        return post("", userId, requestDto);
    }

    //Получение запросов пользователя
    public ResponseEntity<Object> getOwnRequests(Long userId) {
        return get("", userId);
    }

    //Получение запросов других пользователей
    public ResponseEntity<Object> getAllRequests(Long userId) {
        return get("/all", userId);
    }
}