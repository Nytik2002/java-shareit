package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.constants.ShareitConstants;

//Контроллер для работы с бронированиями
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    //Получение бронирований текущего пользователя
    @GetMapping
    public ResponseEntity<Object> getBookings(
            @RequestHeader(ShareitConstants.X_SHARER_USER_ID) long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown state: " + stateParam));

        log.info(
                "Get bookings with state {}, userId={}, from={}, size={}",
                stateParam,
                userId,
                from,
                size
        );

        return bookingClient.getBookings(userId, state, from, size);
    }

    //Создание нового бронирования
    @PostMapping
    public ResponseEntity<Object> bookItem(
            @RequestHeader(ShareitConstants.X_SHARER_USER_ID) long userId,
            @Valid @RequestBody BookItemRequestDto requestDto) {

        log.info("Creating booking {}, userId={}", requestDto, userId);

        return bookingClient.bookItem(userId, requestDto);
    }

    //Получение бронирования по ID
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader(ShareitConstants.X_SHARER_USER_ID) long userId,
            @PathVariable Long bookingId) {

        log.info("Get booking {}, userId={}", bookingId, userId);

        return bookingClient.getBooking(userId, bookingId);
    }

    //Подтверждение или отклонение бронирования
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader(ShareitConstants.X_SHARER_USER_ID) long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {

        log.info(
                "Approve booking {}, approved={}, userId={}",
                bookingId,
                approved,
                userId
        );

        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    //Получение бронирований вещей владельца
    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(ShareitConstants.X_SHARER_USER_ID) long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown state: " + stateParam));

        log.info(
                "Get owner bookings with state {}, userId={}, from={}, size={}",
                stateParam,
                userId,
                from,
                size
        );

        return bookingClient.getOwnerBookings(userId, state, from, size);
    }
}