package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.UserStorage;

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
    public BookingService(BookingStorage bookingStorage, UserStorage userStorage , ItemStorage itemStorage) {
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
                preRez = bookingStorage.findAllBookingByBooker(userId);
                break;
            case "CURRENT":
                preRez = bookingStorage.findAllBookingByBookerCurrent(userId);
                break;
            case "PAST":
                preRez = bookingStorage.findAllBookingByBookerPast(userId);
                break;
            case "FUTURE":
                preRez = bookingStorage.findAllBookingByBookerFuture(userId);
                break;
            case "WAITING":
                preRez = bookingStorage.findAllBookingByBookerWaiting(userId);
                break;
            case "REJECTED":
                preRez = bookingStorage.findAllBookingByBookerRejected(userId);
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
                preRez = bookingStorage.findAllBookingByItemOwner(userId);
                break;
            case "CURRENT":
                preRez = bookingStorage.findAllBookingByItemOwnerCurrent(userId);
                break;
            case "PAST":
                preRez = bookingStorage.findAllBookingByItemOwnerPast(userId);
                break;
            case "FUTURE":
                preRez = bookingStorage.findAllBookingByItemOwnerFuture(userId);
                break;
            case "WAITING":
                preRez = bookingStorage.findAllBookingByItemOwnerWaiting(userId);
                break;
            case "REJECTED":
                preRez = bookingStorage.findAllBookingByItemOwnerRejected(userId);
                break;
            default:
                throw new BadParametrException("Unknown state: UNSUPPORTED_STATUS");
        }
        return preRez.stream().
                map(BookingMapper::toBookingDto).
                collect(Collectors.toList());
    }
}
