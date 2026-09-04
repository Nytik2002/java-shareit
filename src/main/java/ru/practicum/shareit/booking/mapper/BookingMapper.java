package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

//Маппер для преобразования Booking в DTO
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        Long itemId = null;
        Long bookerId = null;
        String status = null;

        if (booking.getItem() != null) {
            itemId = booking.getItem().getId();
        }

        if (booking.getBooker() != null) {
            bookerId = booking.getBooker().getId();
        }

        if (booking.getStatus() != null) {
            status = booking.getStatus().name();
        }

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(itemId)
                .bookerId(bookerId)
                .status(status)
                .build();
    }

    private BookingMapper() {
    }
}