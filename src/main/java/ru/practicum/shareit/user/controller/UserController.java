package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.EntityAction;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        return userMapper.toDto(userService.findById(id));
    }

    @PostMapping
    @Validated(EntityAction.OnCreate.class)
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        User user = userMapper.toModel(userDto);
        return userMapper.toDto(userService.create(user));
    }

    @PatchMapping("/{id}")
    @Validated(EntityAction.OnUpdate.class)
    public UserDto update(@PathVariable long id, @RequestBody @Valid UserDto userDto) {
        userDto.setId(id);
        User user = userMapper.toModel(userDto);
        return userMapper.toDto(userService.update(user));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }
}
