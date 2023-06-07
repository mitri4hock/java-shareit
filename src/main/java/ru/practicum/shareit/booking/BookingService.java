package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.UserStorage;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public BookingService(BookingStorage bookingStorage, UserStorage userStorage, ItemStorage itemStorage) {
        this.bookingStorage = bookingStorage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    public BookingDto saveBooking(Booking booking, Long userId) {
        booking.setBooker(userStorage.findById(userId).get());
        booking.setStatus(EnumStatusBooking.WAITING);
        var rez = bookingStorage.save(booking);
        return BookingMapper.toBookingDto(rez);
    }

    public Optional<Booking> findBookingById(Long id) {
        var preRez = bookingStorage.findById(id);
        if (preRez.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(preRez.get());

    }

    public BookingDto updateApproved(Long bookingId, Boolean approved) {
        var rez = bookingStorage.findById(bookingId);
        if (approved == true) {
            rez.get().setStatus(EnumStatusBooking.APPROVED);
        } else {
            rez.get().setStatus(EnumStatusBooking.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingStorage.save(rez.get()));
    }

    public List<BookingDto> findAllBookingWithStatus(Long userId, String state) {
        List<Booking> preRez;
        switch (state) {
            case "ALL":
                preRez = bookingStorage.findByBooker_IdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                preRez = bookingStorage.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        Date.from(Instant.now()), Date.from(Instant.now()));
                break;
            case "PAST":
                preRez = bookingStorage.findByBooker_IdAndEndBeforeOrderByStartDesc(userId, Date.from(Instant.now()));
                break;
            case "FUTURE":
                preRez = bookingStorage.findByBooker_IdAndStartAfterOrderByStartDesc(userId, Date.from(Instant.now()));
                break;
            case "WAITING":
                preRez = bookingStorage.findByBooker_IdAndStatusOrderByStartDesc(userId, EnumStatusBooking.WAITING);
                break;
            case "REJECTED":
                preRez = bookingStorage.findByBooker_IdAndStatusOrderByStartDesc(userId, EnumStatusBooking.REJECTED);
                break;
            default:
                throw new BadParametrException("Unknown state: UNSUPPORTED_STATUS");
        }
        return preRez.stream().
                map(BookingMapper::toBookingDto).
                collect(Collectors.toList());
    }

    public long countItemForUser(Long userId) {
        return itemStorage.countDistinctByOwner_Id(userId);
    }

    public List<BookingDto> findAllBookingForUserWithStatus(Long userId, String state) {
        List<Booking> preRez;
        switch (state) {
            case "ALL":
                preRez = bookingStorage.findByItemId_IdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                preRez = bookingStorage.findByItemId_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        Date.from(Instant.now()), Date.from(Instant.now()));
                break;
            case "PAST":
                preRez = bookingStorage.findByItemId_IdAndEndBeforeOrderByStartDesc(userId, Date.from(Instant.now()));
                break;
            case "FUTURE":
                preRez = bookingStorage.findByItemId_IdAndStartAfterOrderByStartDesc(userId, Date.from(Instant.now()));
                break;
            case "WAITING":
                preRez = bookingStorage.findByItemId_IdAndStatusOrderByStartDesc(userId, EnumStatusBooking.WAITING);
                break;
            case "REJECTED":
                preRez = bookingStorage.findByItemId_IdAndStatusOrderByStartDesc(userId, EnumStatusBooking.REJECTED);
                break;
            default:
                throw new BadParametrException("Unknown state: UNSUPPORTED_STATUS");
        }
        return preRez.stream().
                map(BookingMapper::toBookingDto).
                collect(Collectors.toList());
    }
}
