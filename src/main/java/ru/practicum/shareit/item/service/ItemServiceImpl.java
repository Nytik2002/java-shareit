package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//Реализация сервиса для работы с вещами
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    //Создание вещи
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new RuntimeException("Item name is required");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new RuntimeException("Item description is required");
        }

        if (itemDto.getAvailable() == null) {
            throw new RuntimeException("Item available status is required");
        }

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);

        return ItemMapper.toItemDto(savedItem);
    }

    //Обновление вещи
    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Only owner can update item");
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
                .orElseThrow(() -> new RuntimeException("Item not found"));

        ItemDto itemDto = ItemMapper.toItemDto(item);

        //Данные бронирований показываем только владельцу вещи
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            Booking lastBooking = bookingRepository
                    .findFirstByItem_IdAndStatusAndEndBeforeOrderByEndDesc(
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime now = LocalDateTime.now();

        return itemRepository.findAllByOwner_Id(userId).stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);

                    Booking lastBooking = bookingRepository
                            .findFirstByItem_IdAndStatusAndEndBeforeOrderByEndDesc(
                                    item.getId(),
                                    BookingStatus.APPROVED,
                                    now)
                            .orElse(null);

                    Booking nextBooking = bookingRepository
                            .findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(
                                    item.getId(),
                                    BookingStatus.APPROVED,
                                    now)
                            .orElse(null);

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
}