package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
public class BookingDto {
    @Min(0)
    private Long id;
    @NotNull
    private Date start;
    @NotNull
    private Date end;
    @NotNull
    private Item item;
    @NotNull
    private User booker;
    @NotNull
    private EnumStatusBooking status;
}
