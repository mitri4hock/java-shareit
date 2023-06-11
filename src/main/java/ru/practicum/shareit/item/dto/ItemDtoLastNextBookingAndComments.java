package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoSmallBooker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDtoLastNextBookingAndComments {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long owner;
    private Long request;
    BookingDtoSmallBooker lastBooking;
    BookingDtoSmallBooker nextBooking;
    List<CommentDto> comments;
}
