package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//Реализация сервиса для работы с вещами
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    //Создание вещи
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);

        return ItemMapper.toItemDto(savedItem);
    }

    //Обновление вещи
    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can update item");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);

        return ItemMapper.toItemDto(updatedItem);
    }

    //Получение вещи по ID
    @Override
    public ItemDto getById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        ItemDto itemDto = ItemMapper.toItemDto(item);

        //Получаем комментарии вещи
        itemDto.setComments(getComments(itemId));

        //Данные бронирований для владельца вещи
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            Booking lastBooking = bookingRepository
                    .findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(
                            itemId,
                            BookingStatus.APPROVED,
                            now)
                    .orElse(null);

            Booking nextBooking = bookingRepository
                    .findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(
                            itemId,
                            BookingStatus.APPROVED,
                            now)
                    .orElse(null);

            if (lastBooking != null) {
                BookingShortDto lastBookingDto = BookingShortDto.builder()
                        .id(lastBooking.getId())
                        .bookerId(lastBooking.getBooker().getId())
                        .build();

                itemDto.setLastBooking(lastBookingDto);
            }

            if (nextBooking != null) {
                BookingShortDto nextBookingDto = BookingShortDto.builder()
                        .id(nextBooking.getId())
                        .bookerId(nextBooking.getBooker().getId())
                        .build();

                itemDto.setNextBooking(nextBookingDto);
            }
        }

        return itemDto;
    }

    //Получение всех вещей владельца
    @Override
    public List<ItemDto> getAllByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        LocalDateTime now = LocalDateTime.now();

        //Получаем сразу все вещи владельца
        List<Item> items = itemRepository.findAllByOwner_Id(userId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        //Получаем комментарии сразу для всех вещей и группируем по ID вещи
        Map<Long, List<Comment>> commentsByItem = commentRepository
                .findByItemIn(items, Sort.by(Sort.Direction.ASC, "created"))
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId()
                ));

        //Получаем подтверждённые бронирования сразу для всех вещей
        Map<Long, List<Booking>> bookingsByItem = bookingRepository
                .findByItemInAndStatus(
                        items,
                        BookingStatus.APPROVED,
                        Sort.by(Sort.Direction.ASC, "start"))
                .stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getItem().getId()
                ));

        return items.stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);

                    //Берём комментарии из Map без нового запроса в базу
                    List<CommentDto> comments = commentsByItem
                            .getOrDefault(item.getId(), Collections.emptyList())
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList());

                    itemDto.setComments(comments);

                    //Берём бронирования из Map без нового запроса в базу
                    List<Booking> itemBookings = bookingsByItem
                            .getOrDefault(item.getId(), Collections.emptyList());

                    Booking lastBooking = null;
                    Booking nextBooking = null;

                    for (Booking booking : itemBookings) {
                        if (booking.getStart().isBefore(now)) {
                            if (lastBooking == null
                                    || booking.getStart().isAfter(lastBooking.getStart())) {
                                lastBooking = booking;
                            }
                        }

                        if (booking.getStart().isAfter(now)) {
                            if (nextBooking == null
                                    || booking.getStart().isBefore(nextBooking.getStart())) {
                                nextBooking = booking;
                            }
                        }
                    }

                    if (lastBooking != null) {
                        itemDto.setLastBooking(BookingShortDto.builder()
                                .id(lastBooking.getId())
                                .bookerId(lastBooking.getBooker().getId())
                                .build());
                    }

                    if (nextBooking != null) {
                        itemDto.setNextBooking(BookingShortDto.builder()
                                .id(nextBooking.getId())
                                .bookerId(nextBooking.getBooker().getId())
                                .build());
                    }

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    //Поиск вещей по тексту
    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    //Добавление комментария
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        LocalDateTime now = LocalDateTime.now();

        boolean hasCompletedBooking =
                bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                        userId,
                        itemId,
                        BookingStatus.APPROVED,
                        now);

        if (!hasCompletedBooking) {
            throw new ValidationException("User has not completed booking");
        }

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(now)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(savedComment);
    }

    //Получение комментариев вещи
    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findAllByItem_IdOrderByCreatedAsc(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}