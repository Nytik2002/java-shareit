package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

//Интерфейс для работы
public interface BookingService {

    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByBooker(Long userId, BookingState state);

    List<BookingDto> getAllByOwner(Long userId, BookingState state);
}