package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.comment.CommentDto;

@Slf4j
@Validated
@RequiredArgsConstructor
@Controller
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findByOwnerIdWithComments(@RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId) {
        log.info("Get items with ownerId={}", ownerId);
        return itemClient.findByOwnerId(ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findByIdWithCommentsAndBookings(@PathVariable(name = "id") @Positive long itemId) {
        log.info("Get item with id={}", itemId);
        return itemClient.findById(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(@RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                                             @RequestParam(name = "text", defaultValue = "") String query) {
        log.info("Get items with ownerId={}, text={}", ownerId, query);
        return itemClient.findByText(ownerId, query);
    }

    @PostMapping
    @Validated(ItemAction.OnCreate.class)
    public ResponseEntity<Object> create(@RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                                         @RequestBody @Valid ItemDto itemDto) {
        log.info("Create item with ownerId={}, dto {}", ownerId, itemDto);
        return itemClient.create(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    @Validated(ItemAction.OnUpdate.class)
    public ResponseEntity<Object> update(@PathVariable long id,
                                         @RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                                         @RequestBody @Valid ItemDto itemDto) {
        log.info("Update item with id={}, ownerId={}, dto {}", id, ownerId, itemDto);
        return itemClient.update(id, ownerId, itemDto);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(@PathVariable(name = "id") long itemId,
                                                @RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                                                @RequestBody @Valid CommentDto commentDto) {
        log.info("Create comment to item with itemId={}, ownerId={}, dto {}", itemId, ownerId, commentDto);
        return itemClient.createComment(itemId, ownerId, commentDto);
    }
}
