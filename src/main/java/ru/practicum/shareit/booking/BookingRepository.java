package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//Репозиторий для работы с бронированиями
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    //Бронирования пользователя
    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfter(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort);

    List<Booking> findByBooker_IdAndEndBefore(
            Long bookerId,
            LocalDateTime end,
            Sort sort);

    List<Booking> findByBooker_IdAndStartAfter(
            Long bookerId,
            LocalDateTime start,
            Sort sort);

    List<Booking> findByBooker_IdAndStatus(
            Long bookerId,
            BookingStatus status,
            Sort sort);

    //Бронирования вещей владельца
    List<Booking> findByItem_Owner_Id(Long ownerId, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort);

    List<Booking> findByItem_Owner_IdAndEndBefore(
            Long ownerId,
            LocalDateTime end,
            Sort sort);

    List<Booking> findByItem_Owner_IdAndStartAfter(
            Long ownerId,
            LocalDateTime start,
            Sort sort);

    List<Booking> findByItem_Owner_IdAndStatus(
            Long ownerId,
            BookingStatus status,
            Sort sort);

    //Получение бронирований для списка вещей
    List<Booking> findByItemInAndStatus(
            List<Item> items,
            BookingStatus status,
            Sort sort);

    //Последнее подтвержденное бронирование вещи
    Optional<Booking> findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(
            Long itemId,
            BookingStatus status,
            LocalDateTime time);

    //Ближайшее подтвержденное бронирование вещи
    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId,
            BookingStatus status,
            LocalDateTime time);

    //Проверка подтвержденного бронирования пользователя
    boolean existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
            Long bookerId,
            Long itemId,
            BookingStatus status,
            LocalDateTime time);
}