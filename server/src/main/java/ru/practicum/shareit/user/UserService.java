package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(long id);

    User create(User obj);

    User update(User obj);

    void delete(long id);
}
