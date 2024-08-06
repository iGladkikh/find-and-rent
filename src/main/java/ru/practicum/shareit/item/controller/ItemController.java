package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.common.EntityAction;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @GetMapping
    public List<ItemDto> findByOwnerId(@RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId) {
        return itemService.findByOwnerId(ownerId).stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable long id) {
        return itemMapper.toDto(itemService.findById(id));
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                                    @RequestParam(name = "text") String query) {
        if (query == null || query.isEmpty()) {
            return findByOwnerId(ownerId);
        }
        return itemService.findByText(query).stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @PostMapping
    @Validated(EntityAction.OnCreate.class)
    public ItemDto create(@RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                          @RequestBody @Valid ItemDto itemDto) {
        Item item = itemMapper.toModel(itemDto);
        item.setOwnerId(ownerId);
        return itemMapper.toDto(itemService.create(item));
    }

    @PatchMapping("/{id}")
    @Validated(EntityAction.OnUpdate.class)
    public ItemDto update(@PathVariable long id,
                          @RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                          @RequestBody @Valid ItemDto itemDto) {
        itemDto.setId(id);
        Item item = itemMapper.toModel(itemDto);
        item.setOwnerId(ownerId);
        return itemMapper.toDto(itemService.update(item));
    }
}
