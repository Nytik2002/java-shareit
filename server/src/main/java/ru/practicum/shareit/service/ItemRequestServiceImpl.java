package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.ItemRequest;
import ru.practicum.shareit.ItemRequestRepository;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//Реализация сервиса для работы с запросами вещей
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

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

    //Получение запросов пользователя
    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<ItemRequest> requests =
                itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(userId);

        return getRequestsWithItems(requests);
    }

    //Получение запросов других пользователей
    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<ItemRequest> requests =
                itemRequestRepository.findAllByRequestor_IdNotOrderByCreatedDesc(userId);

        return getRequestsWithItems(requests);
    }

    //Получение одного запроса по ID
    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        List<Item> items = itemRepository.findAllByRequest_Id(requestId);

        return ItemRequestMapper.toItemRequestDto(request, items);
    }

    //Добавление вещей-ответов к запросам
    private List<ItemRequestDto> getRequestsWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findAllByRequest_IdIn(requestIds);

        Map<Long, List<Item>> itemsByRequest = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId()
                ));

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(
                        request,
                        itemsByRequest.getOrDefault(
                                request.getId(),
                                Collections.emptyList()
                        )
                ))
                .collect(Collectors.toList());
    }
}