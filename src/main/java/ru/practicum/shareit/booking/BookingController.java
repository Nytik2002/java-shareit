package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.constants.ShareitConstants;

import java.util.List;

//Контроллер для работы с бронированиями
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    //Создание нового бронирования
    @PostMapping
    public BookingDto create(
            @RequestHeader(ShareitConstants.XSharerID) Long userId,
            @RequestBody NewBookingRequest bookingRequest) {
        return bookingService.create(userId, bookingRequest);
    }

    //Подтверждение или отклонение бронирования
    @PatchMapping("/{bookingId}")
    public BookingDto approve(
            @RequestHeader(ShareitConstants.XSharerID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    //Получение бронирования по ID
    @GetMapping("/{bookingId}")
    public BookingDto getById(
            @RequestHeader(ShareitConstants.XSharerID) Long userId,
            @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    //Получение текущего пользователя
    @GetMapping
    public List<BookingDto> getAllByBooker(
            @RequestHeader(ShareitConstants.XSharerID) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllByBooker(userId, state);
    }

    //Получение вещей владельца
    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(
            @RequestHeader(ShareitConstants.XSharerID) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllByOwner(userId, state);
    }
}