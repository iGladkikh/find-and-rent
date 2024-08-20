package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.common.LoggerMessagePattern;
import ru.practicum.shareit.common.exception.DataNotFoundException;
import ru.practicum.shareit.common.exception.ForbiddenException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Item> findByOwnerId(long ownerId) {
        log.debug(LoggerMessagePattern.DEBUG, "findItemsByOwnerId", ownerId);
        try {
            return itemRepository.findByOwner_Id(ownerId);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findItemsByOwnerId", ownerId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Item findById(long id) {
        log.debug(LoggerMessagePattern.DEBUG, "findItemById", id);
        try {
            return itemRepository.findById(id).orElseThrow(() ->
                    new DataNotFoundException("Вещь с id: %d не найдена".formatted(id)));
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findItemById", id, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public List<Item> findByText(String query) {
        log.debug(LoggerMessagePattern.DEBUG, "findItemByText", query);
        try {
            return itemRepository.findByText(query);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findItemByText", query, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Item create(long ownerId, Item item) {
        log.debug(LoggerMessagePattern.DEBUG, "createItem", item);
        try {
            Optional<User> ownerOptional = userRepository.findById(ownerId);
            if (ownerOptional.isEmpty()) {
                throw new DataNotFoundException("Пользователь с id: %d не найден".formatted(ownerId));
            }

            item.setOwner(ownerOptional.get());
            return itemRepository.save(item);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "createItem", item, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Item update(long ownerId, Item item) {
        log.debug(LoggerMessagePattern.DEBUG, "updateItem", item);
        try {
            checkUserForExists(ownerId);

            Long id = item.getId();
            Item oldItem = itemRepository.findById(id).orElseThrow(() ->
                    new DataNotFoundException("Вещь с id: %d не найдена".formatted(id)));

            checkUserForEditPermissions(ownerId, oldItem);

            if (item.getName() != null) {
                oldItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                oldItem.setDescription(item.getDescription());
            }
            if (item.getIsAvailable() != null) {
                oldItem.setIsAvailable(item.getIsAvailable());
            }

            return itemRepository.save(oldItem);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "updateItem", item, e.getMessage(), e.getClass());
            throw e;
        }
    }

    private void checkUserForExists(long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new DataNotFoundException("Пользователь с id: %d не найден".formatted(id));
        }
    }

    private void checkUserForEditPermissions(long userId, Item item) {
        if (userId != item.getOwner().getId()) {
            throw new ForbiddenException("Пользователь с id: %d не имеет прав на редактирование вещи с id: %d".formatted(userId, item.getId()));
        }
    }
}
