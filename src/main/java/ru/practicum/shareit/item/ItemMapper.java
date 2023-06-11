package ru.practicum.shareit.item;
import ru.practicum.shareit.booking.dto.BookingDtoSmallBooker;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNextBookingAndComments;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    private static final String valueIfNotProvided = "not provided";
    private static final Long valueIfNotProvidedRequest = -1L;

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName() != null ? item.getName() : valueIfNotProvided)
                .description(item.getDescription() != null ? item.getDescription() : valueIfNotProvided)
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .build();
    }

    public static ItemDtoLastNextBookingAndComments toItemDtoLastNextBookingAndComments(
            Item item,
            BookingDtoSmallBooker lastBooking,
            BookingDtoSmallBooker nextBooking,
            List<CommentDto> comments) {
        return ItemDtoLastNextBookingAndComments.builder()
                .id(item.getId())
                .name(item.getName() != null ? item.getName() : valueIfNotProvided)
                .description(item.getDescription() != null ? item.getDescription() : valueIfNotProvided)
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

}
