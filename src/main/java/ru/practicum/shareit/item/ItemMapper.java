package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNextBooking;
import ru.practicum.shareit.item.model.Item;

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

    public static ItemDtoLastNextBooking toItemDtoLastNextBooking(Item item, BookingDto lastBooking, BookingDto nextBooking) {
        return ItemDtoLastNextBooking.builder()
                .id(item.getId())
                .name(item.getName() != null ? item.getName() : valueIfNotProvided)
                .description(item.getDescription() != null ? item.getDescription() : valueIfNotProvided)
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

}
