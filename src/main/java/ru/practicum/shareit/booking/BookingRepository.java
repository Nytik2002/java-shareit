package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

//Репозиторий для работы с бронированиями
public interface BookingRepository extends JpaRepository<Booking, Long> {

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
}