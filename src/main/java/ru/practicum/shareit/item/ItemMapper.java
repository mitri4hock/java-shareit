package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName() != null ? item.getName() : null)
                .description(item.getDescription() != null ? item.getDescription() : null)
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build()
                ;
    }

    public static Item toItem(ItemDto itemDto, Long itemId, Long userId) {
        return Item.builder()
                .id(itemId)
                .name(itemDto.getName() != null ? itemDto.getName() : null)
                .description(itemDto.getDescription() != null ? itemDto.getDescription() : null)
                .available(itemDto.getAvailable())
                .owner(userId)
                .request(itemDto.getRequest() != null ? itemDto.getRequest() : null)
                .build()
                ;
    }
}
