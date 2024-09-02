package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.LoggerMessagePattern;
import ru.practicum.shareit.common.exception.DataNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository,
                              UserService userService,
                              @Lazy ItemService itemService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public List<Request> findAll() {
        log.debug(LoggerMessagePattern.DEBUG, "findAllRequests");
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            return requestRepository.findAll(sort);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findAllRequests", null, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public List<Request> findByRequestorId(long requestorId) {
        log.debug(LoggerMessagePattern.DEBUG, "findItemsByRequestorId", requestorId);
        try {
            return requestRepository.findByRequestor_IdOrderByCreatedAtDesc(requestorId);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findItemsByRequestorId", requestorId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Request findById(long id) {
        log.debug(LoggerMessagePattern.DEBUG, "findById", id);
        try {
            return requestRepository.findById(id).orElseThrow(() ->
                    new DataNotFoundException("Запрос с id: %d не найден".formatted(id)));
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findById", id, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public RequestDto findByIdWithItems(long requestId) {
        log.debug(LoggerMessagePattern.DEBUG, "findByIdWithItems", requestId);
        try {
            Request request = findById(requestId);
            List<Item> items = itemService.findByRequestId(requestId);
            List<ItemDto> itemDtos = ItemMapper.toDto(items);
            return RequestMapper.toDto(request, itemDtos);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findByIdWithItems", requestId, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Request create(long requestorId, Request request) {
        log.debug(LoggerMessagePattern.DEBUG, "createRequest", request);
        try {
            User requestor = userService.findById(requestorId);
            request.setRequestor(requestor);
            request.setCreatedAt(Instant.now());
            return requestRepository.save(request);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "createRequest", request, e.getMessage(), e.getClass());
            throw e;
        }
    }
}
