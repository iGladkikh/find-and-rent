package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.common.LoggerMessagePattern;
import ru.practicum.shareit.common.exception.DataNotFoundException;
import ru.practicum.shareit.common.exception.ForbiddenException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final BookingService bookingService;
    private final RequestService requestService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserService userService,
                           CommentService commentService,
                           BookingService bookingService,
                           @Lazy RequestService requestService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.commentService = commentService;
        this.bookingService = bookingService;
        this.requestService = requestService;
    }

    @Override
    public List<Item> findByOwnerId(long ownerId) {
        log.debug(LoggerMessagePattern.DEBUG, "findItemsByOwnerId", ownerId);
        try {
            return itemRepository.findByOwner_Id(ownerId);
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "findItemsByOwnerId", ownerId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public List<Item> findByRequestId(long requestId) {
        log.debug(LoggerMessagePattern.DEBUG, "findItemsByRequestId", requestId);
        try {
            return itemRepository.findByRequest_Id(requestId);
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "findItemsByRequestId", requestId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Item findById(long id) {
        log.debug(LoggerMessagePattern.DEBUG, "findItemById", id);
        try {
            return itemRepository.findById(id).orElseThrow(() ->
                    new DataNotFoundException("Вещь с id: %d не найдена".formatted(id)));
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "findItemById", id, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public List<Item> findByText(String query) {
        log.debug(LoggerMessagePattern.DEBUG, "findItemByText", query);
        try {
            return itemRepository.findByText(query);
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "findItemByText", query, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public List<ItemDto> findByOwnerIdWithComments(long ownerId) {
        List<Item> items = findByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).toList();

        Map<Long, List<Comment>> itemIdToComments = commentService.findByItemIds(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> (ItemDto) ItemMapper.toDto(item,
                        CommentMapper.toDto(itemIdToComments.getOrDefault(item.getId(), Collections.emptyList())))
                )
                .toList();
    }

    @Override
    public ItemDto findByIdWithCommentsAndBookings(long itemId) {
        Item item = findById(itemId);
        List<Comment> comments = commentService.findByItemIds(List.of(itemId));
        List<CommentDto> commentsDto = CommentMapper.toDto(comments);

        Booking lastBooking = bookingService.findLastForItem(itemId);
        Booking nextBooking = bookingService.findNextForItem(itemId);
        BookingDto lastBookingDto = lastBooking != null ? BookingMapper.toDto(lastBooking) : null;
        BookingDto nextBookingDto = nextBooking != null ? BookingMapper.toDto(nextBooking) : null;

        return ItemMapper.toDto(item, commentsDto, lastBookingDto, nextBookingDto);
    }

    @Override
    public Item create(long ownerId, Item item, Optional<Long> requestId) {
        log.debug(LoggerMessagePattern.DEBUG, "createItem", item);
        try {
            User owner = userService.findById(ownerId);
            item.setOwner(owner);

            if (requestId.isPresent()) {
                Request request = requestService.findById(requestId.get());
                item.setRequest(request);
            }
            return itemRepository.save(item);
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "createItem", item, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Item update(long ownerId, Item item) {
        log.debug(LoggerMessagePattern.DEBUG, "updateItem", item);
        try {
            userService.findById(ownerId);

            Long id = item.getId();
            Item oldItem = itemRepository.findById(id).orElseThrow(() ->
                    new DataNotFoundException("Вещь с id: %d не найдена".formatted(id)));

            checkUserForEditPermissions(ownerId, oldItem);

            if (item.getName() != null) {
                oldItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                oldItem.setDescription(item.getDescription());
            }
            if (item.getIsAvailable() != null) {
                oldItem.setIsAvailable(item.getIsAvailable());
            }

            return itemRepository.save(oldItem);
        } catch (Exception e) {
            log.error(LoggerMessagePattern.ERROR, "updateItem", item, e.getMessage(), e.getClass());
            throw e;
        }
    }


    private void checkUserForEditPermissions(long userId, Item item) {
        if (userId != item.getOwner().getId()) {
            throw new ForbiddenException("Пользователь с id: %d не имеет прав на редактирование вещи с id: %d".formatted(userId, item.getId()));
        }
    }
}
