package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Validated
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> findByRequestorId(@RequestHeader(name = "X-Sharer-User-Id") @Positive long requestorId) {
        log.info("Get requests with requestorId={}", requestorId);
        return requestClient.findByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll() {
        log.info("Get all requests");
        return requestClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable(name = "id") long requestId) {
        log.info("Get request with id={}", requestId);
        return requestClient.findById(requestId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(name = "X-Sharer-User-Id") @Positive long requestorId,
                                         @RequestBody @Valid RequestDto requestDto) {
        log.info("Create request with requestorId={}, dto {}", requestorId, requestDto);
        return requestClient.create(requestorId, requestDto);
    }
}
