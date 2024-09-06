package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank(groups = {UserAction.OnCreate.class})
    @Size(min = 3, groups = {UserAction.OnCreate.class, UserAction.OnUpdate.class})
    private String name;
    @Email
    @NotBlank(groups = {UserAction.OnCreate.class})
    private String email;
}
