package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

//Маппер для преобразования Booking в DTO
@UtilityClass
public class BookingMapper {

    //Преобразование Booking в BookingDto
    public BookingDto toBookingDto(Booking booking) {
        ItemShortDto item = null;
        BookerDto booker = null;

        if (booking.getItem() != null) {
            item = ItemShortDto.builder()
                    .id(booking.getItem().getId())
                    .name(booking.getItem().getName())
                    .build();
        }

        if (booking.getBooker() != null) {
            booker = BookerDto.builder()
                    .id(booking.getBooker().getId())
                    .name(booking.getBooker().getName())
                    .build();
        }

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(booker)
                .status(booking.getStatus())
                .build();
    }
}