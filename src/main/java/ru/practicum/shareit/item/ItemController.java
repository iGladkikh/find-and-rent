package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.common.EntityAction;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;
    private final BookingService bookingService;

    @Autowired
    public ItemController(ItemService itemService, CommentService commentService, BookingService bookingService) {
        this.itemService = itemService;
        this.commentService = commentService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<ItemDto> findByOwnerIdWithComments(@RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId) {
        List<Item> items = itemService.findByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).toList();

        Map<Long, List<Comment>> commentMap = commentService.findByItemIds(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> (ItemDto) ItemMapper.toDto(item,
                        CommentMapper.toDto(commentMap.getOrDefault(item.getId(), Collections.emptyList())))
                )
                .toList();
    }

    @GetMapping("/{id}")
    public ItemDto findByIdWithCommentsAndBookingsDto(@PathVariable(name = "id") long itemId) {
        Item item = itemService.findById(itemId);
        List<Comment> comments = commentService.findByItemIds(List.of(itemId));
        List<CommentDto> commentsDto = CommentMapper.toDto(comments);

        Booking lastBooking = bookingService.findLastForItem(itemId);
        Booking nextBooking = bookingService.findNextForItem(itemId);
        BookingDto lastBookingDto = lastBooking != null ? BookingMapper.toDto(lastBooking) : null;
        BookingDto nextBookingDto = nextBooking != null ? BookingMapper.toDto(nextBooking) : null;

        return ItemMapper.toDto(item, commentsDto, lastBookingDto, nextBookingDto);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                                    @RequestParam(name = "text") String query) {
        if (query == null || query.isEmpty()) {
            return findByOwnerIdWithComments(ownerId);
        }
        return ItemMapper.toDto(itemService.findByText(query));
    }

    @PostMapping("/{id}/comment")
    public CommentDto createComment(@PathVariable(name = "id") long itemId,
                                    @RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                                    @RequestBody CommentDto commentDto) {
        Comment comment = CommentMapper.toModel(commentDto);
        return CommentMapper.toDto(commentService.create(itemId, ownerId, comment));
    }

    @PostMapping
    @Validated(EntityAction.OnCreate.class)
    public ItemDto create(@RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                          @RequestBody @Valid ItemDto itemDto) {
        Item item = ItemMapper.toModel(itemDto);
        return ItemMapper.toDto(itemService.create(ownerId, item));
    }

    @PatchMapping("/{id}")
    @Validated(EntityAction.OnUpdate.class)
    public ItemDto update(@PathVariable long id,
                          @RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                          @RequestBody @Valid ItemDto itemDto) {
        itemDto.setId(id);
        Item item = ItemMapper.toModel(itemDto);
        return ItemMapper.toDto(itemService.update(ownerId, item));
    }
}
