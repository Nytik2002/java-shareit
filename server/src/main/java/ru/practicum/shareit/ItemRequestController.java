package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.constants.ShareitConstants;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.service.ItemRequestService;

import java.util.List;

//Контроллер для работы с запросами вещей
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    //Создание нового запроса вещи
    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(ShareitConstants.XSharerID) Long userId,
            @RequestBody ItemRequestDto requestDto) {

        return itemRequestService.create(userId, requestDto);
    }

    //Получение запросов пользователя
    @GetMapping
    public List<ItemRequestDto> getOwnRequests(
            @RequestHeader(ShareitConstants.XSharerID) Long userId) {

        return itemRequestService.getOwnRequests(userId);
    }

    //Получение запросов других пользователей
    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader(ShareitConstants.XSharerID) Long userId) {

        return itemRequestService.getAllRequests(userId);
    }

    //Получение одного запроса по ID
    @GetMapping("/{requestId}")
    public ItemRequestDto getById(
            @RequestHeader(ShareitConstants.XSharerID) Long userId,
            @PathVariable Long requestId) {

        return itemRequestService.getById(userId, requestId);
    }
}