package ru.practicum.shareit.common;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    List<T> findAll();

    Optional<T> findById(long id);

    T create(T obj);

    T update(T obj);

    void delete(long id);
}
