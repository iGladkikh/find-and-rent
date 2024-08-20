package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingDto> findByBookerIdUsingStateFilter(
            @RequestHeader(name = "X-Sharer-User-Id") @Positive long bookerId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingStateFilter filter) {
        return BookingMapper.toDto(bookingService.findByBookerIdUsingStateFilter(bookerId, filter));
    }

    @GetMapping("owner")
    public List<BookingDto> findByOwnerIdUsingStateFilter(
            @RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingStateFilter filter) {
        return BookingMapper.toDto(bookingService.findByOwnerIdUsingStateFilter(ownerId, filter));
    }

    @GetMapping("/{id}")
    public BookingDto findById(@PathVariable(name = "id") long bookingId,
                               @RequestHeader(name = "X-Sharer-User-Id") @Positive long userId) {
        return BookingMapper.toDto(bookingService.findByIdUsingUserId(bookingId, userId));
    }

    @PostMapping
    public BookingDto create(@RequestHeader(name = "X-Sharer-User-Id") @Positive long bookerId,
                             @RequestBody @Valid BookingRequestDto bookingDto) {
        Booking booking = bookingService.create(
                bookerId,
                bookingDto.getItemId(),
                Instant.from(bookingDto.getStart().atZone(ZoneId.systemDefault())),
                Instant.from(bookingDto.getEnd().atZone(ZoneId.systemDefault()))
        );
        return BookingMapper.toDto(booking);
    }

    @PatchMapping("/{id}")
    public BookingDto approve(@PathVariable(name = "id") long bookingId,
                              @RequestHeader(name = "X-Sharer-User-Id") @Positive long ownerId,
                              @RequestParam(name = "approved") Boolean isApproved) {
        return BookingMapper.toDto(bookingService.approve(bookingId, ownerId, isApproved));
    }
}
