package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    Item findById(long id);

    Item create(long ownerId, Item obj);

    Item update(long ownerId, Item obj);

    List<Item> findByOwnerId(long ownerId);

    List<Item> findByText(String query);
}
