package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, Instant start, Instant end);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, Instant end);

    List<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(Long bookerId, Instant start);

    List<Booking> findByBooker_IdAndStatusEqualsOrderByStartDesc(Long bookerId, BookingState state);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId, Instant start, Instant end);

    List<Booking> findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, Instant end);

    List<Booking> findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(Long ownerId, Instant start);

    List<Booking> findByItem_Owner_IdAndStatusEqualsOrderByStartDesc(Long ownerId, BookingState state);

    List<Booking> findByBooker_IdAndItem_IdAndEndIsBefore(Long bookerId, Long itemId, Instant end);

    Booking findFirstByItem_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long itemId, Instant start, Instant end);

    Booking findFirstByItem_IdAndStartIsAfterOrderByStartAsc(Long itemId, Instant start);
}
