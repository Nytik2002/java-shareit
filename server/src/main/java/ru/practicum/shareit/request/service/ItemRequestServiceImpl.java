package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;

//Реализация сервиса для работы с запросами вещей
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    //Создание нового запроса вещи
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto requestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest itemRequest = ItemRequest.builder()
                .description(requestDto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(
                savedRequest,
                Collections.emptyList()
        );
    }
}