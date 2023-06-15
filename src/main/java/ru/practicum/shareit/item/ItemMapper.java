package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoSmallBooker;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNextBookingAndComments;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemMapper {

    private final String descriptionIfEnterDescriptionIsNull = "not provided";

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName() != null ? item.getName() : descriptionIfEnterDescriptionIsNull)
                .description(item.getDescription() != null ? item.getDescription() : descriptionIfEnterDescriptionIsNull)
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .build();
    }

    public ItemDtoLastNextBookingAndComments toItemDtoLastNextBookingAndComments(
            Item item,
            BookingDtoSmallBooker lastBooking,
            BookingDtoSmallBooker nextBooking,
            List<CommentDto> comments) {
        return ItemDtoLastNextBookingAndComments.builder()
                .id(item.getId())
                .name(item.getName() != null ? item.getName() : descriptionIfEnterDescriptionIsNull)
                .description(item.getDescription() != null ? item.getDescription() : descriptionIfEnterDescriptionIsNull)
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

}
