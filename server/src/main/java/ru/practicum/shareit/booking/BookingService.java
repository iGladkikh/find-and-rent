package ru.practicum.shareit.booking;

import java.time.Instant;
import java.util.List;

public interface BookingService {

    List<Booking> findByBookerIdAndState(long bookerId, BookingStateFilter state);

    List<Booking> findByOwnerIdAndState(long ownerId, BookingStateFilter state);

    Booking findByIdAndUserId(long bookingId, long userId);

    Booking findLastForItem(long itemId);

    Booking findNextForItem(long itemId);

    Booking create(long bookerId, long itemId, Instant start, Instant end);

    Booking approve(long bookingId, long ownerId, boolean isApproved);
}
