package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class RequestServiceImplTest {
    private final RequestService requestService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void findAll() {
        User user = userService.create(makeTestUser());
        Request request = makeTestRequest();
        request.setRequestor(user);
        requestService.create(user.getId(), request);

        List<Request> requests = requestService.findAll();

        assertFalse(requests.isEmpty());
    }

    @Test
    void findByRequestorId() {
        User user = userService.create(makeTestUser());
        Request request = makeTestRequest();
        request.setRequestor(user);
        requestService.create(user.getId(), request);

        List<Request> requests = requestService.findByRequestorId(user.getId());

        assertFalse(requests.isEmpty());
    }

    @Test
    void findById() {
        User user = userService.create(makeTestUser());
        Request request = makeTestRequest();
        request.setRequestor(user);
        Request saved = requestService.create(user.getId(), request);

        Request result = requestService.findById(saved.getId());

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
    }

    @Test
    void findByIdWithItems() {
        User user = userService.create(makeTestUser());

        Request req = makeTestRequest();
        Request request = requestService.create(user.getId(), req);

        Item item = makeTestItem();
        itemService.create(user.getId(), item, Optional.of(request.getId()));

        RequestWithItemsDto result = requestService.findByIdWithItems(request.getId());

        assertNotNull(result);
        assertFalse(result.getItems().isEmpty());
    }

    @Test
    void create() {
        User user = userService.create(makeTestUser());
        Request request = makeTestRequest();
        request.setRequestor(user);

        Request result = requestService.create(user.getId(), request);
        assertNotNull(result);
    }

    private static Request makeTestRequest() {
        Request request = new Request();
        request.setDescription("request description");
        request.setCreatedAt(Instant.now());
        return request;
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
}