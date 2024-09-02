package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RequestWithItemsDto extends RequestDto {
    private List<ItemDto> items;
}
