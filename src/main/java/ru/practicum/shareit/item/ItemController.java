package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto,
                             @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") Long userId,
                             @PathVariable Long itemId) {
        return itemService.patchItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public Set<ItemDto> getAllMyItems(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") Long userId) {
        return itemService.getAllMyItems(userId);
    }

    @GetMapping("/search") //search?text={text}
    public List<ItemDto> findItem(@RequestParam(value = "text") String text) {
        return itemService.findItem(text);

    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItem(@PathVariable Long itemId) {
        return itemService.deleteItem(itemId);
    }

}
