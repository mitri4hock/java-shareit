package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemStorage {
    ItemDto createItem(ItemDto itemDto, Long userId);

    Item getItem(Long itemId);

    ItemDto patchItem(ItemDto itemDto, Long itemId);

    Set<ItemDto> getAllMyItems(Long userId);

    List<ItemDto> findItem(String text);

    ItemDto deleteItem(Long itemId);
}
