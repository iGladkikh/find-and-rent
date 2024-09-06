package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.DataNotFoundException;
import ru.practicum.shareit.common.exception.DuplicatedDataException;
import ru.practicum.shareit.common.LoggerMessagePattern;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        log.debug(LoggerMessagePattern.DEBUG, "findAllUsers", null);
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "findAllUsers", null, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public User findById(long id) {
        log.debug(LoggerMessagePattern.DEBUG, "findUserById", id);
        try {
            return userRepository.findById(id).orElseThrow(() ->
                    new DataNotFoundException("Пользователь с id: %d не найден".formatted(id)));
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "findUserById", id, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public User create(User user) {
        log.debug(LoggerMessagePattern.DEBUG, "createUser", user);
        try {
            checkEmailForExists(user);
            return userRepository.save(user);
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "createUser", user, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public User update(User user) {
        log.debug(LoggerMessagePattern.DEBUG, "updateUser", user);
        try {
            Long id = user.getId();
            checkUserForExists(id);

            User oldItem = findById(id);

            if (user.getName() == null) {
                user.setName(oldItem.getName());
            }
            if (user.getEmail() == null) {
                user.setEmail(oldItem.getEmail());
            }

            checkEmailForExists(user);
            return userRepository.save(user);
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "updateUser", user, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public void delete(long id) {
        log.debug(LoggerMessagePattern.DEBUG, "deleteUser", id);
        try {
            checkUserForExists(id);
            userRepository.deleteById(id);
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "deleteUser", id, e.getMessage(), e.getClass());
            throw e;
        }
    }

    private void checkEmailForExists(User user) {
        User userWithSameEmail = userRepository.findUserByEmailEqualsIgnoreCase(user.getEmail());
        if (userWithSameEmail != null && !Objects.equals(userWithSameEmail.getId(), user.getId())) {
            throw new DuplicatedDataException("Email %s уже используется".formatted(user.getEmail()));
        }
    }

    private void checkUserForExists(long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new DataNotFoundException("Пользователь с id: %d не найден".formatted(id));
        }
    }
}
