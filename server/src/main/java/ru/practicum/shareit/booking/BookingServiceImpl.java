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
import ru.practicum.shareit.user.UserRole;

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
    public List<Booking> findByBookerIdAndState(long bookerId, BookingStateFilter filter) {
        log.debug(LoggerMessagePattern.DEBUG, "findByBookerIdUsingStateFilter", bookerId);
        try {
            return findByUserIdUsingStateFilter(bookerId, UserRole.BOOKER, filter);
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "findByBookerIdUsingStateFilter", bookerId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public List<Booking> findByOwnerIdAndState(long ownerId, BookingStateFilter filter) {
        log.debug(LoggerMessagePattern.DEBUG, "findByOwnerIdUsingStateFilter", ownerId);
        try {
            return findByUserIdUsingStateFilter(ownerId, UserRole.OWNER, filter);
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "findByOwnerIdUsingStateFilter", ownerId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    private List<Booking> findByUserIdUsingStateFilter(long userId, UserRole userRole, BookingStateFilter filter) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new DataNotFoundException("Пользователь с id: %d не найден".formatted(userId));
        }

        Instant now = Instant.now();
        return switch (filter) {
            case ALL -> userRole == UserRole.BOOKER ?
                    bookingRepository.findByBooker_IdOrderByStartDesc(userId) :
                    bookingRepository.findByItem_Owner_IdOrderByStartDesc(userId);
            case CURRENT -> userRole == UserRole.BOOKER ?
                    bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now) :
                    bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now);
            case PAST -> userRole == UserRole.BOOKER ?
                    bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, now) :
                    bookingRepository.findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(userId, now);
            case FUTURE -> userRole == UserRole.BOOKER ?
                    bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(userId, now) :
                    bookingRepository.findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(userId, now);
            case WAITING -> userRole == UserRole.BOOKER ?
                    bookingRepository.findByBooker_IdAndStatusEqualsOrderByStartDesc(userId, BookingState.WAITING) :
                    bookingRepository.findByItem_Owner_IdAndStatusEqualsOrderByStartDesc(userId, BookingState.WAITING);
            case REJECTED -> userRole == UserRole.BOOKER ?
                    bookingRepository.findByBooker_IdAndStatusEqualsOrderByStartDesc(userId, BookingState.REJECTED) :
                    bookingRepository.findByItem_Owner_IdAndStatusEqualsOrderByStartDesc(userId, BookingState.REJECTED);
        };
    }

    @Override
    public Booking findByIdAndUserId(long bookingId, long userId) {
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
            log.error(LoggerMessagePattern.ERROR, "findBookingById", bookingId, e.getMessage(), e.getClass());
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
            log.error(LoggerMessagePattern.ERROR, "findLastBookingForItem", itemId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Booking findNextForItem(long itemId) {
        log.debug(LoggerMessagePattern.DEBUG, "findNextBookingForItem", itemId);
        try {
            return bookingRepository.findFirstByItem_IdAndStartIsAfterOrderByStartAsc(itemId, Instant.now());
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "findNextBookingForItem", itemId, e.getMessage(), e.getClass());
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
            log.error(LoggerMessagePattern.ERROR, "createBooking", bookerId, e.getMessage(), e.getClass());
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
                throw new ForbiddenException("Пользователь с id: %d не является влалельцем вещи с id: %d"
                        .formatted(bookingId, booking.getItem().getId()));
            }

            if (isApproved) {
                booking.setStatus(BookingState.APPROVED);
            } else {
                booking.setStatus(BookingState.REJECTED);
            }

            return bookingRepository.save(booking);
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "approveBooking", bookingId, e.getMessage(), e.getClass());
            throw e;
        }
    }
}
