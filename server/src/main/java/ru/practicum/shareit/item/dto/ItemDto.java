package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.common.BaseDto;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BaseDto owner;
}
