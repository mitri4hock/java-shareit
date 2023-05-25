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
        storage = new TreeMap<>(Comparable::compareTo);
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        storage.put(currentItemId, ItemMapper.toItem(itemDto, currentItemId, userId));
        currentItemId++;
        Item rezultToReturn = storage.get(currentItemId - 1);
        log.info("добавлена вещь {}", rezultToReturn);
        return ItemMapper.toItemDto(rezultToReturn);
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long itemId) {
        Item patchingItem = storage.get(itemId);
        if (itemDto.getName() != null) {
            patchingItem.setName(itemDto.getName());
            log.info("у вещи с id {} заменено название на {}", itemId, patchingItem.getName());
        }
        if (itemDto.getDescription() != null) {
            patchingItem.setDescription(itemDto.getDescription());
            log.info("у вещи с id {} заменено описание на {}", itemId, patchingItem.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            patchingItem.setAvailable(itemDto.getAvailable());
            log.info("у вещи с id {} заменен статус на {}", itemId, patchingItem.getAvailable());
        }
        return ItemMapper.toItemDto(patchingItem);
    }

    @Override
    public Optional<Item> getItem(Long itemId) {
        var preRez = storage.getOrDefault(itemId, null);
        return Optional.ofNullable(preRez);
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
