package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemStorageInMemoryImpl implements ItemStorage {

    private final Map<Long, Item> storage; // хранение вещей- id, вещь
    private Long currentItemId; // текущий ай-ди вещи

    @Autowired
    public ItemStorageInMemoryImpl() {
        this.currentItemId = 1L;
        storage = new HashMap<>();
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        storage.put(currentItemId, ItemMapper.toItem(itemDto, currentItemId, userId));
        currentItemId++;
        Item rezultToReturn = storage.get(currentItemId - 1);
        log.info("добавлена вещь " + rezultToReturn);
        return ItemMapper.toItemDto(rezultToReturn);
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long itemId) {
        Item patchingItem = storage.get(itemId);
        if (itemDto.getName() != null) {
            patchingItem.setName(itemDto.getName());
            log.info("у вещи с id " + itemId + "заменено название на " + patchingItem.getName());
        }
        if (itemDto.getDescription() != null) {
            patchingItem.setDescription(itemDto.getDescription());
            log.info("у вещи с id " + itemId + "заменено описание на " + patchingItem.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            patchingItem.setAvailable(itemDto.getAvailable());
            log.info("у вещи с id " + itemId + "заменен статус на " + patchingItem.getAvailable());
        }
        return ItemMapper.toItemDto(patchingItem);
    }

    @Override
    public Item getItem(Long itemId) {
        return storage.getOrDefault(itemId, null);
    }

    @Override
    public Set<ItemDto> getAllMyItems(Long userId) {
        return storage.values().stream()
                .filter(x -> x.getOwner().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());

    }

    @Override
    public List<ItemDto> findItem(String text) {
        String query = text.toLowerCase().trim();
        var rez = storage.values().stream()
                .filter(x -> x.getAvailable() == true)
                .filter(x -> x.getName().toLowerCase().contains(query)
                        || x.getDescription().toLowerCase().contains(query))
                .sorted(Comparator.comparing(Item::getId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return rez;
    }

    @Override
    public ItemDto deleteItem(Long itemId) {
        ItemDto tempItem = ItemMapper.toItemDto(storage.getOrDefault(itemId, null));
        storage.remove(itemId);
        return tempItem;
    }
}
