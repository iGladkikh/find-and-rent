package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.common.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository extends Repository<User> {

    Optional<User> findAnotherUserWithSameEmail(User user);
}