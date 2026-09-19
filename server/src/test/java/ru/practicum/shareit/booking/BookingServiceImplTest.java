package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    //Проверка неправильных данных при создании бронирования
    @Test
    void createShouldRejectInvalidBookingData() {
        User owner = User.builder()
                .name("Владелец")
                .email("invalid-owner@test.ru")
                .build();

        User booker = User.builder()
                .name("Арендатор")
                .email("invalid-booker@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(booker);

        Item item = Item.builder()
                .name("Дрель")
                .description("Дрель для проверки")
                .available(false)
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

        //Недоступную вещь бронировать нельзя
        assertThrows(
                ValidationException.class,
                () -> bookingService.create(savedBooker.getId(), request)
        );

        savedItem.setAvailable(true);
        itemRepository.save(savedItem);

        //Владелец не может забронировать свою вещь
        assertThrows(
                NotFoundException.class,
                () -> bookingService.create(savedOwner.getId(), request)
        );

        //Дата начала обязательна
        request.setStart(null);

        assertThrows(
                ValidationException.class,
                () -> bookingService.create(savedBooker.getId(), request)
        );

        //Дата окончания обязательна
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(null);

        assertThrows(
                ValidationException.class,
                () -> bookingService.create(savedBooker.getId(), request)
        );

        //Начало бронирования не может быть в прошлом
        request.setStart(LocalDateTime.now().minusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(
                ValidationException.class,
                () -> bookingService.create(savedBooker.getId(), request)
        );

        //Дата окончания должна быть позже даты начала
        request.setStart(LocalDateTime.now().plusDays(2));
        request.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(
                ValidationException.class,
                () -> bookingService.create(savedBooker.getId(), request)
        );
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

    //Отклонение бронирования и проверки прав владельца
    @Test
    void approveShouldRejectBookingAndCheckErrors() {
        User owner = User.builder()
                .name("Владелец")
                .email("approve-owner@test.ru")
                .build();

        User booker = User.builder()
                .name("Арендатор")
                .email("approve-booker@test.ru")
                .build();

        User stranger = User.builder()
                .name("Посторонний")
                .email("approve-stranger@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(booker);
        User savedStranger = userRepository.save(stranger);

        Item item = Item.builder()
                .name("Лобзик")
                .description("Лобзик для проверки")
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

        //Подтвердить бронирование может только владелец
        assertThrows(
                ForbiddenException.class,
                () -> bookingService.approve(
                        savedStranger.getId(),
                        savedBooking.getId(),
                        true
                )
        );

        //Параметр approved обязателен
        assertThrows(
                ValidationException.class,
                () -> bookingService.approve(
                        savedOwner.getId(),
                        savedBooking.getId(),
                        null
                )
        );

        //Владелец может отклонить бронирование
        BookingDto rejected = bookingService.approve(
                savedOwner.getId(),
                savedBooking.getId(),
                false
        );

        assertEquals(
                BookingStatus.REJECTED,
                rejected.getStatus()
        );

        //Повторно обработать бронирование нельзя
        assertThrows(
                ValidationException.class,
                () -> bookingService.approve(
                        savedOwner.getId(),
                        savedBooking.getId(),
                        true
                )
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

    //Получение бронирования владельцем и запрет постороннему пользователю
    @Test
    void getByIdShouldAllowOwnerAndRejectStranger() {
        User owner = User.builder()
                .name("Владелец")
                .email("get-owner@test.ru")
                .build();

        User booker = User.builder()
                .name("Арендатор")
                .email("get-booker@test.ru")
                .build();

        User stranger = User.builder()
                .name("Посторонний")
                .email("get-stranger@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(booker);
        User savedStranger = userRepository.save(stranger);

        Item item = Item.builder()
                .name("Шлифовальная машина")
                .description("Для проверки доступа")
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

        BookingDto ownerResult = bookingService.getById(
                savedOwner.getId(),
                savedBooking.getId()
        );

        assertEquals(
                savedBooking.getId(),
                ownerResult.getId()
        );

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(
                        savedStranger.getId(),
                        savedBooking.getId()
                )
        );
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

    //Получение бронирований во всех состояниях
    @Test
    void getAllShouldSupportEveryBookingState() {
        User owner = User.builder()
                .name("Владелец состояний")
                .email("states-owner@test.ru")
                .build();

        User booker = User.builder()
                .name("Арендатор состояний")
                .email("states-booker@test.ru")
                .build();

        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(booker);

        Item item = Item.builder()
                .name("Инструмент")
                .description("Инструмент для проверки состояний")
                .available(true)
                .owner(savedOwner)
                .build();

        Item savedItem = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        Booking currentBooking = Booking.builder()
                .start(now.minusHours(1))
                .end(now.plusHours(2))
                .item(savedItem)
                .booker(savedBooker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking pastBooking = Booking.builder()
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .item(savedItem)
                .booker(savedBooker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking futureBooking = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(savedItem)
                .booker(savedBooker)
                .status(BookingStatus.WAITING)
                .build();

        Booking rejectedBooking = Booking.builder()
                .start(now.plusDays(3))
                .end(now.plusDays(4))
                .item(savedItem)
                .booker(savedBooker)
                .status(BookingStatus.REJECTED)
                .build();

        bookingRepository.save(currentBooking);
        bookingRepository.save(pastBooking);
        bookingRepository.save(futureBooking);
        bookingRepository.save(rejectedBooking);

        //Состояния бронирований пользователя
        assertEquals(
                1,
                bookingService.getAllByBooker(
                        savedBooker.getId(),
                        BookingState.CURRENT
                ).size()
        );

        assertEquals(
                1,
                bookingService.getAllByBooker(
                        savedBooker.getId(),
                        BookingState.PAST
                ).size()
        );

        assertEquals(
                2,
                bookingService.getAllByBooker(
                        savedBooker.getId(),
                        BookingState.FUTURE
                ).size()
        );

        assertEquals(
                1,
                bookingService.getAllByBooker(
                        savedBooker.getId(),
                        BookingState.WAITING
                ).size()
        );

        assertEquals(
                1,
                bookingService.getAllByBooker(
                        savedBooker.getId(),
                        BookingState.REJECTED
                ).size()
        );

        //Состояния бронирований вещей владельца
        assertEquals(
                1,
                bookingService.getAllByOwner(
                        savedOwner.getId(),
                        BookingState.CURRENT
                ).size()
        );

        assertEquals(
                1,
                bookingService.getAllByOwner(
                        savedOwner.getId(),
                        BookingState.PAST
                ).size()
        );

        assertEquals(
                2,
                bookingService.getAllByOwner(
                        savedOwner.getId(),
                        BookingState.FUTURE
                ).size()
        );

        assertEquals(
                1,
                bookingService.getAllByOwner(
                        savedOwner.getId(),
                        BookingState.WAITING
                ).size()
        );

        assertEquals(
                1,
                bookingService.getAllByOwner(
                        savedOwner.getId(),
                        BookingState.REJECTED
                ).size()
        );
    }
}