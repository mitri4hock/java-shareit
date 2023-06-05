package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    public BookingController(BookingService bookingService, UserService userService, ItemService itemService) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Autowired



    @PostMapping
    public BookingDto createBooking(@RequestBody @Valid Booking booking,
                                    @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {
        if (booking == null) {
            throw new BadParametrException("при бронировании не было передано тело запроса");
        }
        if (itemService.getItem(booking.getItem().getId()).getId() == null) {
            throw new NotFoundParametrException("при бронировании, была указана несуществующая вещь");
        }
        if (userService.getUserById(userId).isEmpty()) {
            throw new NotFoundParametrException("при бронировании, был указан несуществующий пользователь владелец");
        }

        return bookingService.createBooking(booking, userId);

    }


}
