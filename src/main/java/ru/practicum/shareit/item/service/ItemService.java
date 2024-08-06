package ru.practicum.shareit.item.service;

import ru.practicum.shareit.common.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService extends Service<Item> {

    List<Item> findByOwnerId(long ownerId);

    List<Item> findByText(String query);
}
