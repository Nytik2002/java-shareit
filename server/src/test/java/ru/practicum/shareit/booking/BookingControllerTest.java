package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Тесты контроллера бронирований
@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    //Создание бронирования
    @Test
    void createShouldCallService() throws Exception {
        when(bookingService.create(
                eq(1L),
                any(NewBookingRequest.class)))
                .thenReturn(null);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "itemId": 10,
                                  "start": "2026-09-18T10:00:00",
                                  "end": "2026-09-19T10:00:00"
                                }
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<NewBookingRequest> captor =
                ArgumentCaptor.forClass(NewBookingRequest.class);

        verify(bookingService).create(
                eq(1L),
                captor.capture()
        );

        assertEquals(10L, captor.getValue().getItemId());
    }

    //Подтверждение бронирования
    @Test
    void approveShouldCallService() throws Exception {
        when(bookingService.approve(1L, 5L, true))
                .thenReturn(null);

        mockMvc.perform(patch("/bookings/5")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingService).approve(1L, 5L, true);
    }

    //Получение бронирования по ID
    @Test
    void getByIdShouldCallService() throws Exception {
        when(bookingService.getById(1L, 5L))
                .thenReturn(null);

        mockMvc.perform(get("/bookings/5")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(bookingService).getById(1L, 5L);
    }

    //Получение бронирований пользователя
    @Test
    void getAllByBookerShouldReturnBookings() throws Exception {
        when(bookingService.getAllByBooker(
                1L,
                BookingState.ALL))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(bookingService)
                .getAllByBooker(1L, BookingState.ALL);
    }

    //Получение бронирований вещей владельца
    @Test
    void getAllByOwnerShouldReturnBookings() throws Exception {
        when(bookingService.getAllByOwner(
                1L,
                BookingState.ALL))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(bookingService)
                .getAllByOwner(1L, BookingState.ALL);
    }

    //Состояние ALL используется по умолчанию
    @Test
    void getAllByBookerShouldUseAllStateByDefault() throws Exception {
        when(bookingService.getAllByBooker(
                1L,
                BookingState.ALL))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(bookingService)
                .getAllByBooker(1L, BookingState.ALL);
    }
}