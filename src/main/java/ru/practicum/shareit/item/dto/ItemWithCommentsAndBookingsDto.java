package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.dto.BookingDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemWithCommentsAndBookingsDto extends ItemWithCommentsDto {
    private BookingDto lastBooking;
    private BookingDto nextBooking;
}
