package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    private static final String URI_PATH = "/requests";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final Instant now = Instant.now();

    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private RequestDto requestDto;
    private Request request;
    private User user;

    @BeforeEach
    void setUp() {
        requestDto = new RequestDto();
        requestDto.setDescription("Hummer");

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@mail.com");

        request = RequestMapper.toModel(requestDto);
        request.setId(1L);
        request.setCreatedAt(now);
        request.setRequestor(user);
    }

    @Test
    void findAll() throws Exception {
        when(requestService.findAll())
                .thenReturn(List.of(request));

        mvc.perform(get(URI_PATH + "/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(notNullValue())))
                .andExpect(jsonPath("$[0]", is(notNullValue())))
                .andExpect(jsonPath("$[0].id", is(request.getId()), Long.class));

        verify(requestService, times(1)).findAll();

    }

    @Test
    void findByRequestorId() throws Exception {
        when(requestService.findByRequestorId(anyLong()))
                .thenReturn(List.of(request));

        mvc.perform(get(URI_PATH)
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(notNullValue())))
                .andExpect(jsonPath("$[0]", is(notNullValue())))
                .andExpect(jsonPath("$[0].requestor.id", is(user.getId()), Long.class));

        verify(requestService, times(1)).findByRequestorId(1);
    }

    @Test
    void findByIdWithItems() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setName("Hummer");
        item.setDescription("Good Hummer");
        item.setIsAvailable(true);

        RequestWithItemsDto withItemsDto = RequestMapper.toDto(request, List.of(ItemMapper.toDto(item)));

        when(requestService.findByIdWithItems(anyLong()))
                .thenReturn(withItemsDto);

        mvc.perform(get(URI_PATH + "/" + item.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(notNullValue())))
                .andExpect(jsonPath("$.requestor", is(notNullValue())))
                .andExpect(jsonPath("$.requestor.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.items", is(notNullValue())))
                .andExpect(jsonPath("$.items[0].id", is(item.getId()), Long.class));

        verify(requestService, times(1)).findByIdWithItems(1);
    }

    @Test
    void create() throws Exception {
        when(requestService.create(anyLong(), any(Request.class)))
                .thenReturn(request);

        mvc.perform(post(URI_PATH)
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(LocalDateTime.from(now.atZone(ZoneId.systemDefault())).format(dateTimeFormatter))))
                .andExpect(jsonPath("$.requestor", is(notNullValue())))
                .andExpect(jsonPath("$.requestor.id", is(request.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(request.getRequestor().getName())));

        verify(requestService, times(1)).create(1, RequestMapper.toModel(requestDto));
    }
}