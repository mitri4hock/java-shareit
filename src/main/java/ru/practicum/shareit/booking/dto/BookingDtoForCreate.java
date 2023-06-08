package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class BookingDtoForCreate {
    @NotNull
    private Long itemId;
    @NotNull
    private Date start;
    @NotNull
    private Date end;
}
