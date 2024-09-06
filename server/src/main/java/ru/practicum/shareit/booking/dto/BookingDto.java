package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.common.BaseDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;
    private BaseDto item;
    private BaseDto booker;
    private BookingState status;
}
