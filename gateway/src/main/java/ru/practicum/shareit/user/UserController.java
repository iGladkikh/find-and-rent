package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Validated
@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Get all users");
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable @Positive long id) {
        log.info("Get user with id={}", id);
        return userClient.findById(id);
    }

    @PostMapping
    @Validated(UserAction.OnCreate.class)
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        log.info("Create user with dto {}", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    @Validated(UserAction.OnUpdate.class)
    public ResponseEntity<Object> update(@PathVariable @Positive long id, @RequestBody @Valid UserDto userDto) {
        log.info("Update user with id={}, dto {}", id, userDto);
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable @Positive long id) {
        log.info("Delete user with id={}", id);
        return userClient.delete(id);
    }
}
