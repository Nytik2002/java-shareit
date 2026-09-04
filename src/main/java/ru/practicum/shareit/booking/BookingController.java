package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constants.ShareitConstants;

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
}