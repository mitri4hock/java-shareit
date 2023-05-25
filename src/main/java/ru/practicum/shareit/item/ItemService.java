package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    ItemService(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public Optional<UserDto> getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        return itemStorage.createItem(itemDto, userId);
    }

    public ItemDto patchItem(ItemDto itemDto, Long userId, Long itemId) {

        Optional<Item> tempItem = itemStorage.getItem(itemId);
        if (tempItem.isEmpty()) {
            log.info("Попытка запросить редактирование отсутствующей вещи. itemId= {}", itemId);
            throw new BadParametrException("Отсутствует запрашиваемая вещь. itemId= " + itemId);
        }
        if (!userId.equals(tempItem.get().getOwner())) {
            log.info("Попытка редактировать вещь не её владельцем. Полученный владелец: {}"
                    + " , текущий владелец: {}", userId, tempItem.get().getOwner());
            throw new NotFoundParametrException("Редактировать вещь может только её владелец. Полученный владелец: "
                    + userId + " , текущий владелец: " + tempItem.get().getOwner());
        }
        return itemStorage.patchItem(itemDto, itemId);
    }

    public ItemDto getItem(Long itemId) {
        Optional<Item> tempItem = itemStorage.getItem(itemId);
        if (tempItem.isEmpty()) {
            log.info("Попытка запросить отсутствующую вещь. itemId= {}", itemId);
            throw new BadParametrException("Отсутствует запрашиваемая вещь. itemId= " + itemId);
        }
        return ItemMapper.toItemDto(tempItem.get());
    }

    public Set<ItemDto> getAllMyItems(Long userId) {
        return itemStorage.getAllMyItems(userId);
    }

    public List<ItemDto> findItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.findItem(text);
    }

    public ItemDto deleteItem(Long itemId) {
        return itemStorage.deleteItem(itemId);
    }
}
