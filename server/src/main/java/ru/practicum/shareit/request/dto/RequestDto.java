package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.common.BaseDto;

import java.time.LocalDateTime;

@Data
public class RequestDto {
    Long id;
    @NotBlank
    String description;
    BaseDto requestor;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("created")
    private LocalDateTime createdAt;
}
