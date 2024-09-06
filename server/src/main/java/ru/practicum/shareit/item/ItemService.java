package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    Item findById(long id);

    Item create(long ownerId, Item item, Optional<Long> requestId);

    Item update(long ownerId, Item item);

    List<Item> findByOwnerId(long ownerId);

    List<Item> findByRequestId(long requestId);

    List<Item> findByText(String query);

    List<ItemDto> findByOwnerIdWithComments(long ownerId);

    ItemDto findByIdWithCommentsAndBookings(long itemId);
}
