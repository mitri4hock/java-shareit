package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.util.Constants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated
    public ResponseEntity<Object> bookItem(@RequestHeader(Constants.HEADER_USER_ID_FIELD) long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookings(@RequestHeader(Constants.HEADER_USER_ID_FIELD) long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBooking(@RequestHeader(Constants.HEADER_USER_ID_FIELD) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findAllBookingForUserWithStatus(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestHeader(Constants.HEADER_USER_ID_FIELD) @NotNull Long userId,
            @RequestParam(value = "from", required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false) @Positive Integer size) {
        log.info("Get booking for owner with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookingsOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> verificationBooking(@PathVariable Long bookingId,
                                                      @RequestHeader(Constants.HEADER_USER_ID_FIELD) @NotNull Long userId,
                                                      @RequestParam(value = "approved") Boolean approved) {
        return bookingClient.verificationBooking(bookingId, approved, userId);
    }
}
