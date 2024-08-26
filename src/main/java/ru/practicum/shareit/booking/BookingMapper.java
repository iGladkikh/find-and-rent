package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.common.BaseDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    private static final ZoneId TIMEZONE_ID = ZoneId.systemDefault();

    public static BookingDto toDto(Booking booking) {
        BaseDto item = BaseDto.from(booking.getItem());
        BaseDto booker = BaseDto.from(booking.getBooker());

        return BookingDto.builder()
                .id(booking.getId())
                .start(LocalDateTime.from(booking.getStart().atZone(TIMEZONE_ID)))
                .end(LocalDateTime.from(booking.getEnd().atZone(TIMEZONE_ID)))
                .item(item)
                .booker(booker)
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDto> toDto(Iterable<Booking> bookings) {
        if (bookings == null) return new ArrayList<>();
        List<BookingDto> dtos = new ArrayList<>();
        bookings.forEach(booking -> dtos.add(toDto(booking)));
        return dtos;
    }
}
