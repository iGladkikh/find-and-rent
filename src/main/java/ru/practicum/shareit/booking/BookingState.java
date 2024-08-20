package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum BookingState {
    @JsonEnumDefaultValue
    WAITING,
    APPROVED,
    REJECTED,
    CANCELLED
}
