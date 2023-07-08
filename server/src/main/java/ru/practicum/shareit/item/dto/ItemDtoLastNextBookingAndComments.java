package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoSmallBooker;

import java.util.List;

@Data
@Builder
public class ItemDtoLastNextBookingAndComments {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long request;
    private BookingDtoSmallBooker lastBooking;
    private BookingDtoSmallBooker nextBooking;
    private List<CommentDto> comments;
}
