package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.ConflictParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

        return bookingService.saveBooking(booking, userId);

    }

    @PatchMapping("/{bookingId}")
    public BookingDto verificationBooking(@PathVariable Long bookingId,
                                          @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                                          @RequestParam(value = "approved") Boolean approved) {
        var booking = bookingService.findBookingById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundParametrException("отсутствует бронирование с id = " + bookingId);
        }
        if (booking.get().getItem().getOwner().getId() != userId) {
            throw new ConflictParametrException("Редактировать статус бронирования может только владелец вещи");
        }
        return bookingService.updateApproved(bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {
        var booking = bookingService.findBookingById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundParametrException("отсутствует бронирование с id = " + bookingId);
        }
        if (booking.get().getBooker().getId() != userId || booking.get().getItem().getOwner().getId() != userId) {
            throw new ConflictParametrException("запрашивать бронирование может или автор или владелец вещи");
        }
        return BookingMapper.toBookingDto(booking.get());
    }


    /**
     * Получение списка всех бронирований текущего пользователя. Эндпоинт — GET /bookings?state={state}.
     * Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     * Также он может принимать значения CURRENT (англ. «текущие»), **PAST** (англ. «завершённые»),
     * FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
     * Бронирования должны возвращаться отсортированными по дате от более новых к более старым
     */

    @GetMapping
    public List<BookingDto> findAllBookingWithStatus(
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {
        if (userService.getUserById(userId).isEmpty()) {
            throw new BadParametrException("Запрошены бронирования от несуществующего пользователя. Id = " + userId);
        }
        return bookingService.findAllBookingWithStatus(userId, state);
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя. Эндпоинт — GET /bookings/owner?state={state}.
     * Этот запрос имеет смысл для владельца хотя бы одной вещи. Работа параметра state аналогична его работе в
     * предыдущем сценарии.
     */
    @GetMapping("/owner")
    public List<BookingDto> findAllBookingForUserWithStatus(
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {
        if (userService.getUserById(userId).isEmpty()) {
            throw new BadParametrException("Запрошены бронирования от несуществующего пользователя. Id = " + userId);
        }
        if (bookingService.countItemForUser(userId) == 0L){
            return new ArrayList<>();
        }
        return bookingService.findAllBookingForUserWithStatus(userId, state);
    }

    /**
     * Осталась пара штрихов. Итак, вы добавили возможность бронировать вещи. Теперь нужно, чтобы владелец видел
     * даты последнего и ближайшего следующего бронирования для каждой вещи, когда просматривает список (GET /items).
     */


}
