package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsAndBookingsDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private static final String URI_PATH = "/items";

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private static ItemCreateDto itemDto;
    private static Item item;
    private static User user;
    private static Comment comment;
    private static Booking booking;

    @BeforeAll
    static void setUp() {
        itemDto = createTestDto();

        user = createTestUser();

        item = ItemMapper.toModel(itemDto);
        item.setOwner(user);

        comment = createTestComment();
        comment.setItem(item);
        comment.setAuthor(user);

        booking = createTestBooking();
        booking.setBooker(user);
        booking.setItem(item);
    }

    @Test
    void findByOwnerIdWithComments() throws Exception {
        ItemWithCommentsDto itemWithCommentsDto = ItemMapper.toDto(item, List.of(CommentMapper.toDto(comment)));

        when(itemService.findByOwnerIdWithComments(anyLong()))
                .thenReturn(List.of(itemWithCommentsDto));

        mvc.perform(get(URI_PATH)
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(notNullValue())))
                .andExpect(jsonPath("$[0].comments", is(notNullValue())))
                .andExpect(jsonPath("$[0].comments[0].id", is(comment.getId()), Long.class));
    }

    @Test
    void findByIdWithCommentsAndBookings() throws Exception {
        ItemWithCommentsAndBookingsDto itemWithCommentsAndBookingsDto = ItemMapper.toDto(
                item,
                List.of(CommentMapper.toDto(comment)),
                null,
                BookingMapper.toDto(booking)
        );

        when(itemService.findByIdWithCommentsAndBookings(anyLong()))
                .thenReturn(itemWithCommentsAndBookingsDto);

        mvc.perform(get(URI_PATH + "/" + anyLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.comments", is(notNullValue())))
                .andExpect(jsonPath("$.comments[0].id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(anything())))
                .andExpect(jsonPath("$.nextBooking.id", is(booking.getId()), Long.class));
    }

    @Test
    void findByText() throws Exception {
        when(itemService.findByText(anyString()))
                .thenReturn(List.of(item));

        mvc.perform(get(URI_PATH + "/search")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("text", "text")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", is(notNullValue())))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @Test
    void create() throws Exception {
        when(itemService.create(anyLong(), any(), any()))
                .thenReturn(item);

        mvc.perform(post(URI_PATH)
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.owner", is(notNullValue())))
                .andExpect(jsonPath("$.owner.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(user.getName())));
    }

    @Test
    void createComment() throws Exception {
        when(commentService.create(anyLong(), anyLong(), any()))
                .thenReturn(comment);

        mvc.perform(post(URI_PATH + "/" + item.getId() + "/comment")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(CommentMapper.toDto(comment)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(notNullValue())));
    }

    @Test
    void update() throws Exception {
        when(itemService.update(anyLong(), any()))
                .thenReturn(item);

        mvc.perform(patch(URI_PATH + "/" + itemDto.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    private static Comment createTestComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("New comment");
        comment.setCreatedAt(Instant.now());
        return comment;
    }

    private static User createTestUser() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@mail.com");
        return user;
    }

    private static ItemCreateDto createTestDto() {
        itemDto = new ItemCreateDto();
        itemDto.setId(1L);
        itemDto.setName("Hummer");
        itemDto.setDescription("Good Hummer");
        itemDto.setAvailable(true);
        return itemDto;
    }

    private static Booking createTestBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(Instant.now().plusSeconds(100));
        booking.setEnd(Instant.now().plusSeconds(200));
        return booking;
    }
}