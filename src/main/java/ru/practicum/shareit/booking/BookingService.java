package ru.practicum.shareit.booking;

import java.time.Instant;
import java.util.List;

public interface BookingService {

    List<Booking> findByBookerIdUsingStateFilter(long bookerId, BookingStateFilter filter);

    List<Booking> findByOwnerIdUsingStateFilter(long ownerId, BookingStateFilter filter);

    Booking findByIdUsingUserId(long bookingId, long userId);

    Booking findLastForItem(long itemId);

    Booking findNextForItem(long itemId);

    Booking create(long bookerId, long itemId, Instant start, Instant end);

    Booking approve(long bookingId, long ownerId, boolean isApproved);
}
