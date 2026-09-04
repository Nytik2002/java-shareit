package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//DTO для создания нового бронирования
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewBookingRequest {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}