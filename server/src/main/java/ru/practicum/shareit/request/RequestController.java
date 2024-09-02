package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public List<RequestDto> findByRequestorId(@RequestHeader(name = "X-Sharer-User-Id") long requestorId) {
        return RequestMapper.toDto(requestService.findByRequestorId(requestorId));
    }

    @GetMapping("/all")
    public List<RequestDto> findAll() {
        return RequestMapper.toDto(requestService.findAll());
    }

    @GetMapping("/{id}")
    public RequestDto findByIdWithItems(@PathVariable(name = "id") long requestId) {
        return requestService.findByIdWithItems(requestId);
    }

    @PostMapping
    public RequestDto create(@RequestHeader(name = "X-Sharer-User-Id") long requestorId,
                             @RequestBody RequestDto requestDto) {
        Request request = RequestMapper.toModel(requestDto);
        return RequestMapper.toDto(requestService.create(requestorId, request));
    }
}
