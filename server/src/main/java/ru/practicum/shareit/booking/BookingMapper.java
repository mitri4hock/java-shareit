package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;
import ru.practicum.shareit.booking.dto.BookingDtoSmallBooker;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDto.builder()
                .id(booking.getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public Booking toBooking(BookingDtoForCreate bookingDtoForCreate, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoForCreate.getStart());
        booking.setEnd(bookingDtoForCreate.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(EnumStatusBooking.WAITING);
        return booking;
    }

    public BookingDtoSmallBooker toBookingDtoSmallBooker(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDtoSmallBooker.builder()
                .id(booking.getId())
                .start((booking.getStart()))
                .end(booking.getEnd())
                .item(booking.getItem())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }
}
