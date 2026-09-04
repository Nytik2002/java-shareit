package ru.practicum.shareit.booking.service;

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

import java.time.LocalDateTime;
import java.util.List;

//Реализация сервиса для работы с бронированиями
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

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

    @Override
    public List<BookingDto> getAllByBooker(Long userId, BookingState state) {
        return null;
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, BookingState state) {
        return null;
    }
}