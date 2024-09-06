package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final RequestService requestService;

    @Test
    void findByOwnerId() {
        User user = userService.create(makeTestUser());
        itemService.create(user.getId(), makeTestItem(), Optional.empty());

        List<Item> items = itemService.findByOwnerId(user.getId());
        assertEquals(1, items.size());
    }

    @Test
    void findByRequestId() {
        User user = userService.create(makeTestUser());
        Request request = requestService.create(user.getId(), makeTestRequest());
        itemService.create(user.getId(), makeTestItem(), Optional.of(request.getId()));

        List<Item> result = itemService.findByRequestId(request.getId());

        assertEquals(1, result.size());
    }

    @Test
    void findById() {
        User user = userService.create(makeTestUser());
        Item item = itemService.create(user.getId(), makeTestItem(), Optional.empty());

        Item result = itemService.findById(item.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
    }

    @Test
    void findByText() {
        User user = userService.create(makeTestUser());
        Item item = itemService.create(user.getId(), makeTestItem(), Optional.empty());

        List<Item> result = itemService.findByText(item.getDescription());

        assertEquals(1, result.size());
    }

    @Test
    void create() {
        User user = userService.create(makeTestUser());
        Item result = itemService.create(user.getId(), makeTestItem(), Optional.empty());

        assertNotNull(result);
        assertTrue(result.getId() > 0);
    }

    private static User makeTestUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@mail.com");
        return user;
    }

    private static Item makeTestItem() {
        Item item = new Item();
        item.setName("Hummer");
        item.setDescription("Good Hummer");
        item.setIsAvailable(true);
        return item;
    }

    private static Request makeTestRequest() {
        Request request = new Request();
        request.setDescription("request description");
        request.setCreatedAt(Instant.now());
        return request;
    }
}