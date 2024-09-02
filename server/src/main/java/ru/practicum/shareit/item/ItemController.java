package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @Autowired
    public ItemController(ItemService itemService, CommentService commentService) {
        this.itemService = itemService;
        this.commentService = commentService;
    }

    @GetMapping
    public List<ItemDto> findByOwnerIdWithComments(@RequestHeader(name = "X-Sharer-User-Id") long ownerId) {
        return itemService.findByOwnerIdWithComments(ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto findByIdWithCommentsAndBookings(@PathVariable(name = "id") long itemId) {
        return itemService.findByIdWithCommentsAndBookings(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestHeader(name = "X-Sharer-User-Id") long ownerId,
                                    @RequestParam(name = "text", defaultValue = "") String query) {
        if (query.isBlank()) {
            return findByOwnerIdWithComments(ownerId);
        }
        return ItemMapper.toDto(itemService.findByText(query));
    }

    @PostMapping("/{id}/comment")
    public CommentDto createComment(@PathVariable(name = "id") long itemId,
                                    @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
                                    @RequestBody CommentDto commentDto) {
        Comment comment = CommentMapper.toModel(commentDto);
        return CommentMapper.toDto(commentService.create(itemId, ownerId, comment));
    }

    @PostMapping
    public ItemDto create(@RequestHeader(name = "X-Sharer-User-Id") long ownerId,
                          @RequestBody ItemCreateDto itemDto) {
        Optional<Long> requestId = Optional.ofNullable(itemDto.getRequestId());
        Item item = ItemMapper.toModel(itemDto);
        return ItemMapper.toDto(itemService.create(ownerId, item, requestId));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable long id,
                          @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
                          @RequestBody ItemCreateDto itemDto) {
        itemDto.setId(id);
        Item item = ItemMapper.toModel(itemDto);
        return ItemMapper.toDto(itemService.update(ownerId, item));
    }
}
