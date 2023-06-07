package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDto.builder()
                .id(booking.getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .itemId(booking.getItemId())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDtoForCreate bookingDtoForCreate, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoForCreate.getStart().toInstant());
        booking.setEnd(bookingDtoForCreate.getEnd().toInstant());
        booking.setItemId(item);
        booking.setBooker(booker);
        booking.setStatus(EnumStatusBooking.WAITING);
        return booking;
    }
}
