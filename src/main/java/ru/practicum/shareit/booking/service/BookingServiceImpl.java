package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//Реализация сервиса для работы с бронированиями
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    //Создание нового бронирования
    @Override
    public BookingDto create(Long userId, NewBookingRequest bookingRequest) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getAvailable()) {
            throw new RuntimeException("Item is not available");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Owner cannot book own item");
        }

        if (bookingRequest.getStart() == null) {
            throw new RuntimeException("Start date is required");
        }

        if (bookingRequest.getEnd() == null) {
            throw new RuntimeException("End date is required");
        }

        if (bookingRequest.getStart().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Start date must be in the future");
        }

        if (!bookingRequest.getEnd().isAfter(bookingRequest.getStart())) {
            throw new RuntimeException("End date must be after start date");
        }

        Booking booking = Booking.builder()
                .start(bookingRequest.getStart())
                .end(bookingRequest.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(savedBooking);
    }

    //Подтверждение или отклонение бронирования
    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Only owner can approve booking");
        }

        if (approved == null) {
            throw new RuntimeException("Approved status is required");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new RuntimeException("Booking is already processed");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking updatedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(updatedBooking);
    }

    //Получение бронирования по ID
    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
            throw new RuntimeException("Booking not found");
        }

        return BookingMapper.toBookingDto(booking);
    }

    //Получение бронирований текущего пользователя
    @Override
    public List<BookingDto> getAllByBooker(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        if (state == BookingState.ALL) {
            bookings = bookingRepository.findByBooker_Id(userId, sort);
        } else if (state == BookingState.CURRENT) {
            bookings = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(
                    userId, now, now, sort);
        } else if (state == BookingState.PAST) {
            bookings = bookingRepository.findByBooker_IdAndEndBefore(
                    userId, now, sort);
        } else if (state == BookingState.FUTURE) {
            bookings = bookingRepository.findByBooker_IdAndStartAfter(
                    userId, now, sort);
        } else if (state == BookingState.WAITING) {
            bookings = bookingRepository.findByBooker_IdAndStatus(
                    userId, BookingStatus.WAITING, sort);
        } else if (state == BookingState.REJECTED) {
            bookings = bookingRepository.findByBooker_IdAndStatus(
                    userId, BookingStatus.REJECTED, sort);
        } else {
            throw new RuntimeException("Unknown booking state");
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    //Получение бронирований вещей владельца
    @Override
    public List<BookingDto> getAllByOwner(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        if (state == BookingState.ALL) {
            bookings = bookingRepository.findByItem_Owner_Id(userId, sort);
        } else if (state == BookingState.CURRENT) {
            bookings = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(
                    userId, now, now, sort);
        } else if (state == BookingState.PAST) {
            bookings = bookingRepository.findByItem_Owner_IdAndEndBefore(
                    userId, now, sort);
        } else if (state == BookingState.FUTURE) {
            bookings = bookingRepository.findByItem_Owner_IdAndStartAfter(
                    userId, now, sort);
        } else if (state == BookingState.WAITING) {
            bookings = bookingRepository.findByItem_Owner_IdAndStatus(
                    userId, BookingStatus.WAITING, sort);
        } else if (state == BookingState.REJECTED) {
            bookings = bookingRepository.findByItem_Owner_IdAndStatus(
                    userId, BookingStatus.REJECTED, sort);
        } else {
            throw new RuntimeException("Unknown booking state");
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}