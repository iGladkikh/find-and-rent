package ru.practicum.shareit.common;

import java.util.List;

public interface Service<T> {

    List<T> findAll();

    T findById(long id);

    T create(T obj);

    T update(T obj);

    void delete(long id);
}
