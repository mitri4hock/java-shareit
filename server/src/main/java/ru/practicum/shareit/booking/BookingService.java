package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;

import java.util.List;

public interface BookingService {
    BookingDto saveBooking(BookingDtoForCreate bookingDtoCreate, Long userId);

    BookingDto findBookingById(Long id, Long userId);

    BookingDto updateApproved(Long bookingId, Boolean approved, Long userId);

    List<BookingDto> findAllBookingWithStatus(Long userId, String state, Integer from, Integer size);

    long countItemForUser(Long userId);

    List<BookingDto> findAllBookingForUserWithStatus(Long userId, String state, Integer from, Integer size);
}
