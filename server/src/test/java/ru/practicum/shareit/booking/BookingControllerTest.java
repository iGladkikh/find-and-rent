package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private static final String URI_PATH = "/bookings";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private BookingCreateDto bookingDto;
    private Booking booking;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {

        bookingDto = BookingCreateDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusSeconds(86400))
                .bookerId(1L)
                .itemId(1L)
                .build();

        user = new User();
        user.setId(bookingDto.getBookerId());
        user.setName("John Doe");
        user.setEmail("john.doe@mail.com");

        item = new Item();
        item.setId(bookingDto.getItemId());
        item.setName("Hummer");
        item.setDescription("Good Hummer");
        item.setIsAvailable(true);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(Instant.from(bookingDto.getStart().atZone(ZoneId.systemDefault())));
        booking.setEnd(Instant.from(bookingDto.getEnd().atZone(ZoneId.systemDefault())));
        booking.setBooker(user);
        booking.setItem(item);
    }

    @Test
    void findByBookerIdAndState() throws Exception {
        when(bookingService.findByBookerIdAndState(anyLong(), any(BookingStateFilter.class)))
                .thenReturn(List.of(booking));

        mvc.perform(get(URI_PATH)
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class));

        verify(bookingService, times(1)).findByBookerIdAndState(1, BookingStateFilter.ALL);
    }

    @Test
    void findByOwnerIdAndState() throws Exception {
        when(bookingService.findByOwnerIdAndState(anyLong(), any(BookingStateFilter.class)))
                .thenReturn(List.of(booking));

        mvc.perform(get(URI_PATH + "/owner")
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class));

        verify(bookingService, times(1)).findByOwnerIdAndState(1, BookingStateFilter.ALL);
    }

    @Test
    void findById() throws Exception {
        when(bookingService.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get(URI_PATH + "/" + booking.getId())
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(user.getId()), Long.class));

        verify(bookingService, times(1)).findByIdAndUserId(1, 1);
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(anyLong(), anyLong(), any(Instant.class), any(Instant.class)))
                .thenReturn(booking);

        mvc.perform(post(URI_PATH)
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(dateTimeFormatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(dateTimeFormatter))))
                .andExpect(jsonPath("$.booker", is(notNullValue())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBookerId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(user.getName())))
                .andExpect(jsonPath("$.item", is(notNullValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(item.getName())));

        verify(bookingService, times(1)).create(1, 1,
                booking.getStart().truncatedTo(ChronoUnit.SECONDS),
                booking.getEnd().truncatedTo(ChronoUnit.SECONDS));
    }
}