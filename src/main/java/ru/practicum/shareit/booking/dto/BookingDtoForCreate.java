package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BookingDtoForCreate {
    private Long itemId;
    private Date start;
    private Date end;
}
