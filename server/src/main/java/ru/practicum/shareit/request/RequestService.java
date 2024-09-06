package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestWithItemsDto;

import java.util.List;

public interface RequestService {

    List<Request> findAll();

    List<Request> findByRequestorId(long requestorId);

    Request findById(long id);

    RequestWithItemsDto findByIdWithItems(long id);

    Request create(long requestorId, Request request);
}
