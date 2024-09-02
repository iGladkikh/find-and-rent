package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemDto {
    @NotBlank(groups = {ItemAction.OnCreate.class})
    @Size(min = 3, groups = {ItemAction.OnCreate.class, ItemAction.OnUpdate.class})
    private String name;
    @NotBlank(groups = {ItemAction.OnCreate.class})
    private String description;
    @NotNull(groups = {ItemAction.OnCreate.class})
    private Boolean available;
    private Long requestId;
}
