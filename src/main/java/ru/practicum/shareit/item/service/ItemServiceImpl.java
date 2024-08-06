package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.DataNotFoundException;
import ru.practicum.shareit.common.LoggerMessagePattern;
import ru.practicum.shareit.common.exception.ForbiddenException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Item> findByOwnerId(long ownerId) {
        log.debug(LoggerMessagePattern.DEBUG, "findAllItems", null);
        try {
            return itemRepository.findAll().stream()
                    .filter(item -> item.getOwnerId() == ownerId)
                    .toList();
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findAllItems", null, e.getMessage(), e.getClass());
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
        return itemRepository.findAll().stream()
                .filter(Item::getIsAvailable)
                .filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    @Override
    public Item create(Item item) {
        log.debug(LoggerMessagePattern.DEBUG, "createItem", item);
        try {
            checkUserForExists(item.getOwnerId());

            return itemRepository.create(item);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "createItem", item, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Item update(Item item) {
        log.debug(LoggerMessagePattern.DEBUG, "updateItem", item);
        try {
            Long id = item.getId();
            checkItemForExists(id);

            Long userId = item.getOwnerId();
            checkUserForExists(userId);

            Item oldItem = findById(id);
            checkUserForEditPermissions(userId, oldItem);

            if (item.getName() != null) {
                oldItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                oldItem.setDescription(item.getDescription());
            }
            if (item.getIsAvailable() != null) {
                oldItem.setIsAvailable(item.getIsAvailable());
            }

            return itemRepository.update(oldItem);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "updateItem", item, e.getMessage(), e.getClass());
            throw e;
        }
    }

    private void checkItemForExists(long id) {
        if (itemRepository.findById(id).isEmpty()) {
            throw new DataNotFoundException("Вещь с id: %d не найдена".formatted(id));
        }
    }

    private void checkUserForExists(long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new DataNotFoundException("Пользователь с id: %d не найден".formatted(id));
        }
    }

    private void checkUserForEditPermissions(long userId, Item item) {
        if (userId != item.getOwnerId()) {
            throw new ForbiddenException("Пользователь с id: %d не имеет прав на редактирование".formatted(userId));
        }
    }

    @Override
    public List<Item> findAll() {
        throw new UnsupportedOperationException("Метод findAll() не поддерживается");
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Метод delete() не поддерживается");
    }
}
