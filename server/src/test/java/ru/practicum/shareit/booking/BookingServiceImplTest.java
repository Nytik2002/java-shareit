package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//Интеграционные тесты сервиса бронирований
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    //Создание бронирования
    @Test
    void createShouldSaveBookingWithWaitingStatus() {
        User owner = User.builder()
                .name("Владелец")
                .email("owner-booking@test.ru")
                .build();

        User booker = User.builder()
                .name("Арендатор")
                .email("booker-booking@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(booker);

        Item item = Item.builder()
                .name("Дрель")
                .description("Обычная дрель")
                .available(true)
                .owner(savedOwner)
                .build();

        Item savedItem = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        NewBookingRequest request = NewBookingRequest.builder()
                .itemId(savedItem.getId())
                .start(start)
                .end(end)
                .build();

        BookingDto result =
                bookingService.create(savedBooker.getId(), request);

        assertNotNull(result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(savedItem.getId(), result.getItem().getId());

        Booking savedBooking = bookingRepository
                .findById(result.getId())
                .orElseThrow();

        assertEquals(BookingStatus.WAITING, savedBooking.getStatus());
        assertEquals(savedBooker.getId(), savedBooking.getBooker().getId());
        assertEquals(savedItem.getId(), savedBooking.getItem().getId());
    }

    //Подтверждение бронирования владельцем
    @Test
    void approveShouldChangeStatusToApproved() {
        User owner = User.builder()
                .name("Анна")
                .email("anna-booking@test.ru")
                .build();

        User booker = User.builder()
                .name("Иван")
                .email("ivan-booking@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(booker);

        Item item = Item.builder()
                .name("Перфоратор")
                .description("Мощный перфоратор")
                .available(true)
                .owner(savedOwner)
                .build();

        Item savedItem = itemRepository.save(item);

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(savedItem)
                .booker(savedBooker)
                .status(BookingStatus.WAITING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        BookingDto result = bookingService.approve(
                savedOwner.getId(),
                savedBooking.getId(),
                true
        );

        assertEquals(BookingStatus.APPROVED, result.getStatus());

        Booking updatedBooking = bookingRepository
                .findById(savedBooking.getId())
                .orElseThrow();

        assertEquals(
                BookingStatus.APPROVED,
                updatedBooking.getStatus()
        );
    }

    //Получение бронирования по ID
    @Test
    void getByIdShouldReturnBooking() {
        User owner = User.builder()
                .name("Мария")
                .email("maria-booking@test.ru")
                .build();

        User booker = User.builder()
                .name("Сергей")
                .email("sergey-booking@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(booker);

        Item item = Item.builder()
                .name("Молоток")
                .description("Обычный молоток")
                .available(true)
                .owner(savedOwner)
                .build();

        Item savedItem = itemRepository.save(item);

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(savedItem)
                .booker(savedBooker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        BookingDto result = bookingService.getById(
                savedBooker.getId(),
                savedBooking.getId()
        );

        assertEquals(savedBooking.getId(), result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        assertEquals(savedItem.getId(), result.getItem().getId());
        assertNotNull(result.getBooker());
    }

    //Получение бронирований пользователя от новых к старым
    @Test
    void getAllByBookerShouldReturnBookingsFromNewestToOldest() {
        User owner = User.builder()
                .name("Олег")
                .email("oleg-booking@test.ru")
                .build();

        User booker = User.builder()
                .name("Светлана")
                .email("sveta-booking@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(booker);

        Item item = Item.builder()
                .name("Шуруповёрт")
                .description("Аккумуляторный")
                .available(true)
                .owner(savedOwner)
                .build();

        Item savedItem = itemRepository.save(item);

        Booking oldBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(savedItem)
                .booker(savedBooker)
                .status(BookingStatus.WAITING)
                .build();

        Booking newBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .item(savedItem)
                .booker(savedBooker)
                .status(BookingStatus.WAITING)
                .build();

        Booking savedOldBooking = bookingRepository.save(oldBooking);
        Booking savedNewBooking = bookingRepository.save(newBooking);

        List<BookingDto> result = bookingService.getAllByBooker(
                savedBooker.getId(),
                BookingState.ALL
        );

        assertEquals(2, result.size());
        assertEquals(savedNewBooking.getId(), result.get(0).getId());
        assertEquals(savedOldBooking.getId(), result.get(1).getId());
    }

    //Получение бронирований вещей владельца
    @Test
    void getAllByOwnerShouldReturnOwnersBookings() {
        User owner = User.builder()
                .name("Алексей")
                .email("alex-booking@test.ru")
                .build();

        User booker = User.builder()
                .name("Ольга")
                .email("olga-booking@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(booker);

        Item item = Item.builder()
                .name("Лобзик")
                .description("Электрический лобзик")
                .available(true)
                .owner(savedOwner)
                .build();

        Item savedItem = itemRepository.save(item);

        Booking firstBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(savedItem)
                .booker(savedBooker)
                .status(BookingStatus.WAITING)
                .build();

        Booking secondBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .item(savedItem)
                .booker(savedBooker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking savedFirstBooking =
                bookingRepository.save(firstBooking);

        Booking savedSecondBooking =
                bookingRepository.save(secondBooking);

        List<BookingDto> result = bookingService.getAllByOwner(
                savedOwner.getId(),
                BookingState.ALL
        );

        assertEquals(2, result.size());
        assertEquals(savedSecondBooking.getId(), result.get(0).getId());
        assertEquals(savedFirstBooking.getId(), result.get(1).getId());
    }
}