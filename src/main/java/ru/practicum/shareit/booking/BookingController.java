package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated
    public BookingDto createBooking(@RequestBody @Valid @NotNull BookingDtoForCreate bookingDtoCreate,
                                    @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {
        return bookingService.saveBooking(bookingDtoCreate, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto verificationBooking(@PathVariable Long bookingId,
                                          @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                                          @RequestParam(value = "approved") Boolean approved) {

        return bookingService.updateApproved(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {

        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingWithStatus(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {

        return bookingService.findAllBookingWithStatus(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingForUserWithStatus(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {

        return bookingService.findAllBookingForUserWithStatus(userId, state);
    }

}
