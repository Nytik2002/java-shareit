package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.constants.ShareitConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

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
}