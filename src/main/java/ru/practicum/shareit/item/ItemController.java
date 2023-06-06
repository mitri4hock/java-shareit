package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNextBooking;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    @Autowired
    ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid Item item,
                              @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {

        if (item == null) {
            throw new BadParametrException("при создании вещи не было передано тело запроса");
        }
        if (itemService.getUserById(userId).isEmpty()) {
            throw new NotFoundParametrException("при создании вещи, был указан несуществующий пользователь владелец");
        }

        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody @NotNull Item item,
                             @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                             @PathVariable @NotNull Long itemId) {

        return itemService.patchItem(item, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDtoLastNextBooking> getAllMyItems(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.getAllMyItems(userId);
    }

    @GetMapping("/search") //search?text={text}
    public List<ItemDto> findItem(@RequestParam(value = "text") String text) {
        return itemService.findItem(text);

    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }

}
