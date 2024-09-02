package ru.practicum.shareit.request;

import ru.practicum.shareit.common.BaseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class RequestMapper {
    private static final ZoneId TIMEZONE_ID = ZoneId.systemDefault();

    public static RequestWithItemsDto toDto(Request request, List<ItemDto> itemDtos) {
        BaseDto requestor = BaseDto.from(request.getRequestor());

        RequestWithItemsDto requestDto = new RequestWithItemsDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setRequestor(requestor);
        requestDto.setCreatedAt(LocalDateTime.from(request.getCreatedAt().atZone(TIMEZONE_ID)));
        requestDto.setItems(itemDtos);
        return requestDto;
    }

    public static RequestDto toDto(Request request) {
        BaseDto requestor = BaseDto.from(request.getRequestor());

        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setRequestor(requestor);
        requestDto.setCreatedAt(LocalDateTime.from(request.getCreatedAt().atZone(TIMEZONE_ID)));
        return requestDto;
    }

    public static List<RequestDto> toDto(Iterable<Request> requests) {
        List<RequestDto> dtos = new ArrayList<>();
        requests.forEach(request -> dtos.add(toDto(request)));
        return dtos;
    }

    public static Request toModel(RequestDto requestDto) {
        Request request = new Request();
        request.setDescription(requestDto.getDescription());
        return request;
    }
}
