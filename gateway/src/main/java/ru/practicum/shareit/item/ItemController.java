package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

//Контроллер для обработки HTTP запросов вещей
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    //Создание вещи
    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody NewItemRequest itemRequest) {

        return itemClient.create(userId, itemRequest);
    }

    //Обновление вещи
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemRequest itemRequest) {

        return itemClient.update(userId, itemId, itemRequest);
    }

    //Получение вещи по ID
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {

        return itemClient.getById(userId, itemId);
    }

    //Получение всех вещей владельца
    @GetMapping
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return itemClient.getAllByOwner(userId);
    }

    //Поиск вещей по тексту
    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    //Добавление комментария
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody NewCommentRequest commentRequest) {

        return itemClient.addComment(userId, itemId, commentRequest);
    }
}