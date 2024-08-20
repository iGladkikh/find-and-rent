package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.LoggerMessagePattern;
import ru.practicum.shareit.common.exception.DataNotAvailableException;
import ru.practicum.shareit.common.exception.DataNotFoundException;
import ru.practicum.shareit.common.exception.ForbiddenException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Booking> findByBookerIdUsingStateFilter(long bookerId, BookingStateFilter filter) {
        log.debug(LoggerMessagePattern.DEBUG, "findByBookerIdUsingStateFilter", bookerId);
        try {
            if (userRepository.findById(bookerId).isEmpty()) {
                throw new DataNotFoundException("Пользователь с id: %d не найден".formatted(bookerId));
            }

            Instant now = Instant.now();
            return switch (filter) {
                case ALL -> bookingRepository.findByBooker_IdOrderByStartDesc(bookerId);
                case CURRENT ->
                        bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(bookerId, now, now);
                case PAST -> bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(bookerId, now);
                case FUTURE -> bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(bookerId, now);
                case WAITING ->
                        bookingRepository.findByBooker_IdAndStatusEqualsOrderByStartDesc(bookerId, BookingState.WAITING);
                case REJECTED ->
                        bookingRepository.findByBooker_IdAndStatusEqualsOrderByStartDesc(bookerId, BookingState.REJECTED);
            };
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findByBookerIdUsingStateFilter", bookerId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public List<Booking> findByOwnerIdUsingStateFilter(long ownerId, BookingStateFilter filter) {
        log.debug(LoggerMessagePattern.DEBUG, "findByOwnerIdUsingStateFilter", ownerId);
        try {
            if (userRepository.findById(ownerId).isEmpty()) {
                throw new DataNotFoundException("Пользователь с id: %d не найден".formatted(ownerId));
            }

            Instant now = Instant.now();

            return switch (filter) {
                case ALL -> bookingRepository.findByItem_Owner_IdOrderByStartDesc(ownerId);
                case CURRENT ->
                        bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, now, now);
                case PAST -> bookingRepository.findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(ownerId, now);
                case FUTURE -> bookingRepository.findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(ownerId, now);
                case WAITING ->
                        bookingRepository.findByItem_Owner_IdAndStatusEqualsOrderByStartDesc(ownerId, BookingState.WAITING);
                case REJECTED ->
                        bookingRepository.findByItem_Owner_IdAndStatusEqualsOrderByStartDesc(ownerId, BookingState.REJECTED);
            };
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findByOwnerIdUsingStateFilter", ownerId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Booking findByIdUsingUserId(long bookingId, long userId) {
        log.debug(LoggerMessagePattern.DEBUG, "findBookingById", bookingId);
        try {
            Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
            if (bookingOptional.isEmpty()) {
                throw new DataNotFoundException("Бронь с id: %d не найдена".formatted(bookingId));
            }

            Booking booking = bookingOptional.get();
            if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
                throw new ForbiddenException("Пользователь с id: %d не является владельцем или арендатором вещи с id: %d"
                        .formatted(userId, booking.getItem().getId()));
            }

            return booking;
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findBookingById", bookingId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Booking findLastForItem(long itemId) {
        log.debug(LoggerMessagePattern.DEBUG, "findLastBookingForItem", itemId);
        try {
            Instant now = Instant.now();
            return bookingRepository.findFirstByItem_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(itemId, now, now);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findLastBookingForItem", itemId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Booking findNextForItem(long itemId) {
        log.debug(LoggerMessagePattern.DEBUG, "findNextBookingForItem", itemId);
        try {
            return bookingRepository.findFirstByItem_IdAndStartIsAfterOrderByStartAsc(itemId, Instant.now());
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findNextBookingForItem", itemId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Booking create(long bookerId, long itemId, Instant start, Instant end) {
        log.debug(LoggerMessagePattern.DEBUG, "createBooking", bookerId);
        try {
            Optional<User> bookerOptional = userRepository.findById(bookerId);
            if (bookerOptional.isEmpty()) {
                throw new DataNotFoundException("Пользователь с id: %d не найден".formatted(bookerId));
            }

            Optional<Item> itemOptional = itemRepository.findById(itemId);
            if (itemOptional.isEmpty()) {
                throw new DataNotFoundException("Вещь с id: %d не найдена".formatted(itemId));
            }

            Item item = itemOptional.get();
            if (!item.getIsAvailable()) {
                throw new DataNotAvailableException("Вещь недоступна для бронирования");
            }

            if (end.minusMillis(1).isBefore(start)) {
                throw new ValidationException("Ошибка в сроке бронирования");
            }
            Booking booking = new Booking();
            booking.setStart(start);
            booking.setEnd(end);
            booking.setBooker(bookerOptional.get());
            booking.setItem(item);
            booking.setStatus(BookingState.WAITING);
            return bookingRepository.save(booking);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "createBooking", bookerId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Booking approve(long bookingId, long ownerId, boolean isApproved) {
        log.debug(LoggerMessagePattern.DEBUG, "approveBooking", bookingId);
        try {
            Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
            if (bookingOptional.isEmpty()) {
                throw new DataNotFoundException("Бронь с id: %d не найдена".formatted(bookingId));
            }

            Booking booking = bookingOptional.get();
            if (booking.getItem().getOwner().getId() != ownerId) {
                throw new ForbiddenException("Пользователь с id: %d не является вдалельцем вещи с id: %d"
                        .formatted(bookingId, booking.getItem().getId()));
            }

            if (isApproved) {
                booking.setStatus(BookingState.APPROVED);
            } else {
                booking.setStatus(BookingState.REJECTED);
            }

            return bookingRepository.save(booking);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "approveBooking", bookingId, e.getMessage(), e.getClass());
            throw e;
        }
    }
}
