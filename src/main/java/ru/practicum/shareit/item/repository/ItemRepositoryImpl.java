package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> findAll() {
        return List.copyOf(items.values());
    }

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item create(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item newItem) {
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Метод delete() не поддерживается");
    }

    private long getNextId() {
        long currentMaxId = items.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}