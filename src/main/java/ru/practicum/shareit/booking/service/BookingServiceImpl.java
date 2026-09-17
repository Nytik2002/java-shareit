package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner cannot book own item");
        }

        if (bookingRequest.getStart() == null) {
            throw new ValidationException("Start date is required");
        }

        if (bookingRequest.getEnd() == null) {
            throw new ValidationException("End date is required");
        }

        if (bookingRequest.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date must be in the future");
        }

        if (!bookingRequest.getEnd().isAfter(bookingRequest.getStart())) {
            throw new ValidationException("End date must be after start date");
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
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can approve booking");
        }

        if (approved == null) {
            throw new ValidationException("Approved status is required");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking is already processed");
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
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
            throw new NotFoundException("Booking not found");
        }

        return BookingMapper.toBookingDto(booking);
    }

    //Получение бронирований текущего пользователя
    @Override
    public List<BookingDto> getAllByBooker(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBooker_Id(userId, sort);

            case CURRENT ->
                    bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(
                            userId, now, now, sort);

            case PAST ->
                    bookingRepository.findByBooker_IdAndEndBefore(
                            userId, now, sort);

            case FUTURE ->
                    bookingRepository.findByBooker_IdAndStartAfter(
                            userId, now, sort);

            case WAITING ->
                    bookingRepository.findByBooker_IdAndStatus(
                            userId, BookingStatus.WAITING, sort);

            case REJECTED ->
                    bookingRepository.findByBooker_IdAndStatus(
                            userId, BookingStatus.REJECTED, sort);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    //Получение бронирований вещей владельца
    @Override
    public List<BookingDto> getAllByOwner(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItem_Owner_Id(userId, sort);

            case CURRENT ->
                    bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(
                            userId, now, now, sort);

            case PAST ->
                    bookingRepository.findByItem_Owner_IdAndEndBefore(
                            userId, now, sort);

            case FUTURE ->
                    bookingRepository.findByItem_Owner_IdAndStartAfter(
                            userId, now, sort);

            case WAITING ->
                    bookingRepository.findByItem_Owner_IdAndStatus(
                            userId, BookingStatus.WAITING, sort);

            case REJECTED ->
                    bookingRepository.findByItem_Owner_IdAndStatus(
                            userId, BookingStatus.REJECTED, sort);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}