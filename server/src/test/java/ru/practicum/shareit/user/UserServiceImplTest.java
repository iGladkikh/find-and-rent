package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.DataNotFoundException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class UserServiceImplTest {
    private final UserService service;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = makeTestUser();
    }

    @Test
    void findAll() {
        service.create(testUser);

        List<User> all = service.findAll();

        assertThat(all.size(), greaterThan(0));
    }

    @Test
    void findById() {
        User user = service.create(testUser);

        User result = service.findById(user.getId());

        assertThat(result.getId(), notNullValue());
    }

    @Test
    void create() {
        User user = service.create(testUser);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(testUser.getName()));
        assertThat(user.getEmail(), equalTo(testUser.getEmail()));
    }

    @Test
    void createWithEmptyName() {
        testUser.setName(null);

        assertThrows(Exception.class,
                () -> service.create(testUser));
    }

    @Test
    void update() {
        User user = service.create(testUser);

        String newName = "Abram";
        user.setName(newName);
        user = service.update(user);

        assertThat(user.getName(), equalTo(newName));
    }

    @Test
    void updateWithEmptyNameAndEmail() {
        User user = service.create(testUser);

        //Нужно создать новый объект, тк h2 хранит объекты в пямяти
        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setName(null);
        newUser.setEmail(null);

        User result = service.update(newUser);

        assertThat(result.getName(), notNullValue());
        assertThat(result.getEmail(), notNullValue());
        assertThat(result.getName(), equalTo(user.getName()));
        assertThat(result.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void delete() {
        User user = service.create(testUser);

        service.delete(user.getId());

        assertThrows(DataNotFoundException.class,
                () -> service.findById(user.getId()));
    }

    private static User makeTestUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@mail.com");

        return user;
    }
}