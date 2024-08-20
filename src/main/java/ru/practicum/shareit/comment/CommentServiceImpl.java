package ru.practicum.shareit.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.common.LoggerMessagePattern;
import ru.practicum.shareit.common.exception.DataNotAvailableException;
import ru.practicum.shareit.common.exception.DataNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              BookingRepository bookingRepository) {
        this.commentRepository = commentRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Comment> findByItemIds(Collection<Long> itemIds) {
        return commentRepository.findByItem_IdIn(itemIds);
    }

    @Override
    public Comment create(long itemId, long authorId, Comment comment) {
        log.debug(LoggerMessagePattern.DEBUG, "createComment", comment);
        try {
            Optional<Item> itemOptional = itemRepository.findById(itemId);
            if (itemOptional.isEmpty()) {
                throw new DataNotFoundException("Вещь с id: %d не найдена".formatted(itemId));
            }

            Optional<User> authorOptional = userRepository.findById(authorId);
            if (authorOptional.isEmpty()) {
                throw new DataNotFoundException("Пользователь с id: %d не найден".formatted(authorId));
            }

            Instant now = Instant.now();
            List<Booking> bookings = bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(authorId, itemId, now);
            if (bookings.isEmpty()) {
                throw new DataNotAvailableException(("Пользователь с id: %d не арендовал вещь с id: %d " +
                        "либо аренда не завершена").formatted(authorId, itemId));
            }

            comment.setItem(itemOptional.get());
            comment.setAuthor(authorOptional.get());
            comment.setCreatedAt(now);
            return commentRepository.save(comment);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "createComment", comment, e.getMessage(), e.getClass());
            throw e;
        }
    }
}
