package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    Long id;
    String name;
    String description;
    Boolean isAvailable;
    Long ownerId;
    Long requestId;
}
