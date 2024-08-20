package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.common.BaseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsAndBookingsDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemWithCommentsAndBookingsDto toDto(Item item,
                                                       List<CommentDto> comments,
                                                       BookingDto lastBooking,
                                                       BookingDto nextBooking) {
        ItemWithCommentsAndBookingsDto dto = new ItemWithCommentsAndBookingsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setIsAvailable(item.getIsAvailable());
        dto.setOwner(BaseDto.from(item.getOwner()));
        dto.setComments(comments);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        return dto;
    }

    public static ItemWithCommentsDto toDto(Item item, List<CommentDto> comments) {
        ItemWithCommentsDto dto = new ItemWithCommentsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setIsAvailable(item.getIsAvailable());
        dto.setOwner(BaseDto.from(item.getOwner()));
        dto.setComments(comments);
        return dto;
    }

    public static ItemDto toDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setIsAvailable(item.getIsAvailable());
        dto.setOwner(BaseDto.from(item.getOwner()));
        return dto;
    }

    public static List<ItemDto> toDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        items.forEach(item -> dtos.add(toDto(item)));
        return dtos;
    }

    public static Item toModel(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setIsAvailable(itemDto.getIsAvailable());
        return item;
    }
}
