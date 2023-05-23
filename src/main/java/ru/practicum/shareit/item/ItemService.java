package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
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

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        if (userId == -1L) {
            throw new BadParametrException("при создании вещи не был передан параметр X-Sharer-User-Id");
        }
        if (itemDto == null) {
            throw new BadParametrException("при создании вещи не было передано тело запроса");
        }
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundParametrException("при создании вещи, был указан несуществующий пользователь владелец");
        }
        if (itemDto.getAvailable() == null) {
            throw new BadParametrException("при создании вещи не был указан статус доступности");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new BadParametrException("при создании вещи был указан пустой параметр имени. item: " +
                    itemDto);
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new BadParametrException("при создании вещи был указан пустой параметр описания. item: " +
                    itemDto);
        }
        return itemStorage.createItem(itemDto, userId);
    }

    public ItemDto patchItem(ItemDto itemDto, Long userId, Long itemId) {
        if (userId == -1L) {
            throw new BadParametrException("при обновлении вещи не был передан параметр X-Sharer-User-Id");
        }
        if (itemDto == null || itemId == null) {
            throw new BadParametrException("при обновлении вещи были переданы неверные параметры: "
                    + "item= " + itemDto + " , itemId=" + itemId);
        }

        Item tempItem = itemStorage.getItem(itemId);
        if (tempItem == null) {
            log.info("Попытка запросить редактирование отсутствующей вещи. itemId= " + itemId);
            throw new BadParametrException("Отсутствует запрашиваемая вещь. itemId= " + itemId);
        }
        if (userId != tempItem.getOwner()) {
            log.info("Попытка редактировать вещь не её владельцем. Полученный владелец: "
                    + userId + " , текущий владелец: " + tempItem.getOwner());
            throw new NotFoundParametrException("Редактировать вещь может только её владелец. Полученный владелец: "
                    + userId + " , текущий владелец: " + tempItem.getOwner());
        }
        return itemStorage.patchItem(itemDto, itemId);
    }

    public ItemDto getItem(Long itemId) {
        Item tempItem = itemStorage.getItem(itemId);
        if (tempItem == null) {
            log.info("Попытка запросить отсутствующую вещь. itemId= " + itemId);
            throw new BadParametrException("Отсутствует запрашиваемая вещь. itemId= " + itemId);
        }
        return ItemMapper.toItemDto(tempItem);
    }

    public Set<ItemDto> getAllMyItems(Long userId) {
        if (userId == -1L) {
            throw new BadParametrException("при запросе всех вещей владельца не был передан параметр X-Sharer-User-Id");
        }
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
