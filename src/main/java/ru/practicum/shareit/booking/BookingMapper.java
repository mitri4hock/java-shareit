package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null){
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
}
