package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    public static final String HEADER_USER_ID_FIELD = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated
    public BookingDto createBooking(@RequestBody BookingDtoForCreate bookingDtoCreate,
                                    @RequestHeader(value = HEADER_USER_ID_FIELD) Long userId) {
        return bookingService.saveBooking(bookingDtoCreate, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto verificationBooking(@PathVariable Long bookingId,
                                          @RequestHeader(value = HEADER_USER_ID_FIELD) Long userId,
                                          @RequestParam(value = "approved") Boolean approved) {
        return bookingService.updateApproved(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(value = HEADER_USER_ID_FIELD) Long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> findAllBookingWithStatus(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestHeader(value = HEADER_USER_ID_FIELD) Long userId,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "size", required = false) Integer size) {
        return bookingService.findAllBookingWithStatus(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> findAllBookingForUserWithStatus(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestHeader(value = HEADER_USER_ID_FIELD) Long userId,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "size", required = false) Integer size) {
        return bookingService.findAllBookingForUserWithStatus(userId, state, from, size);
    }

}
