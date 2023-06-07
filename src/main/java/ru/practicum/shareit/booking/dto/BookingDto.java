package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

@Data
@Builder
public class BookingDto {
    private Long id;
    private Instant start;
    private Instant end;
    private Item itemId;
    private User booker;
    private EnumStatusBooking status;
}
