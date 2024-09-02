package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.common.EntityAction;

@Data
public class UserDto {
    private Long id;
    @NotBlank(groups = {EntityAction.OnCreate.class})
    @Size(min = 3, groups = {EntityAction.OnCreate.class, EntityAction.OnUpdate.class})
    private String name;
    @Email
    @NotBlank(groups = {EntityAction.OnCreate.class})
    private String email;
}
