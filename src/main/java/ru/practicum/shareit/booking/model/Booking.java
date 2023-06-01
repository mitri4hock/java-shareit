package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
public class Booking {
    @Min(1)
    private Long id;
    @NotNull
    private Date start;
    @NotNull
    private Date end;
    @NotNull
    private Long item;
    @NotNull
    private Long booker;
    @NotNull
    private EnumStatusBooking status;

}
