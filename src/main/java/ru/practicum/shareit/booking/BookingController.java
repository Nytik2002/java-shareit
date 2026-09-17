package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

//Контроллер для работы с бронированиями
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    //Создание нового бронирования
    @PostMapping
    public BookingDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody NewBookingRequest bookingRequest) {

        return bookingService.create(userId, bookingRequest);
    }

    //Подтверждение или отклонение бронирования
    @PatchMapping("/{bookingId}")
    public BookingDto approve(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {

        return bookingService.approve(userId, bookingId, approved);
    }

    //Получение бронирования по ID
    @GetMapping("/{bookingId}")
    public BookingDto getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {

        return bookingService.getById(userId, bookingId);
    }

    //Получение бронирований текущего пользователя
    @GetMapping
    public List<BookingDto> getAllByBooker(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {

        return bookingService.getAllByBooker(userId, state);
    }

    //Получение бронирований вещей владельца
    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {

        return bookingService.getAllByOwner(userId, state);
    }
}