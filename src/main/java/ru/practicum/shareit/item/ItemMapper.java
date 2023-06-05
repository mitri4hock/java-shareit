package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
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
                //.request(item.getRequest() != null ? item.getRequest().getId() : valueIfNotProvidedRequest)
                .build()
                ;
    }

//    public static Item toItem(ItemDto itemDto, Long itemId, Long userId) {
//        return Item.builder()
//                .id(itemId)
//                .name(itemDto.getName() != null ? itemDto.getName() : valueIfNotProvided)
//                .description(itemDto.getDescription() != null ? itemDto.getDescription() : valueIfNotProvided)
//                .available(itemDto.getAvailable())
//                .owner(userId)
//                .request(itemDto.getRequest() != null ? itemDto.getRequest() : valueIfNotProvidedRequest)
//                .build()
//                ;
//    }
}
