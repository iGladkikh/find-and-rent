package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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

@Slf4j
@Validated
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> findByBookerIdAndState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with userId={}, state={}, from={}, size={}", userId, stateParam, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwnerIdAndState(@RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                                                        @RequestParam(name = "state", defaultValue = "all") String stateParam) {
       BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with ownerId={}, state={}", ownerId, stateParam);
        return bookingClient.findByOwnerIdAndState(ownerId, state);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable Long bookingId) {
        log.info("Get booking with id={}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid BookingDto bookingDto) {
        log.info("Create booking with userId={}, Dto {}", userId, bookingDto);
        return bookingClient.bookItem(userId, bookingDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> approve(@PathVariable(name = "id") long bookingId,
                                          @RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                                          @RequestParam(name = "approved") Boolean isApproved) {
        log.info("Approve booking with id={}, ownerId={}, approved={}", bookingId, ownerId, isApproved);
        return bookingClient.approve(bookingId, ownerId, isApproved);
    }
}