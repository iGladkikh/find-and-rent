package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestDto {
    @NotBlank
    String description;
}
