package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.user.UserStorage;

@Service
@Slf4j
public class BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;

    public BookingService(BookingStorage bookingStorage, UserStorage userStorage) {
        this.bookingStorage = bookingStorage;
        this.userStorage = userStorage;
    }

    @Autowired


    public BookingDto createBooking(Booking booking, Long userId) {
        booking.setBooker(userStorage.findById(userId).get());
        booking.setStatus(EnumStatusBooking.WAITING);
        var rez = bookingStorage.save(booking);
        return BookingMapper.toBookingDto(rez);
    }
}
