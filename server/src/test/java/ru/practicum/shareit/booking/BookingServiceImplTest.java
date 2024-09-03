package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class BookingServiceImplTest {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void create() {
        User user = userService.create(makeTestUser());
        Item item = itemService.create(user.getId(), makeTestItem(), Optional.empty());

        Booking result = bookingService.create(
                user.getId(),
                item.getId(),
                Instant.now().plusSeconds(100),
                Instant.now().plusSeconds(200)
        );

        assertNotNull(result);
    }

    @Test
    void approve() {
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