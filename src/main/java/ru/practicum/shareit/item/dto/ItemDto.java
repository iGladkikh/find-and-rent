package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.common.EntityAction;

@Data
@Builder
public class ItemDto {
    Long id;

    @NotBlank(groups = {EntityAction.OnCreate.class})
    @Size(min = 3, groups = {EntityAction.OnCreate.class, EntityAction.OnUpdate.class})
    String name;

    @NotBlank(groups = {EntityAction.OnCreate.class})
    String description;

    @JsonProperty("available")
    @NotNull(groups = {EntityAction.OnCreate.class})
    Boolean isAvailable;
}
