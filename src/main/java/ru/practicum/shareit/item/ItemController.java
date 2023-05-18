package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto
            , @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto
            , @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") Long userId
            , @PathVariable Long itemId) {
        return itemService.patchItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    /**
     * Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой. Эндпойнт GET /items.
     */
    @GetMapping
    public Set<ItemDto> getAllMyItems(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") Long userId) {
        return itemService.getAllMyItems(userId);
    }

    /**
     * Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст, и система ищет вещи,
     * содержащие этот текст в названии или описании. Происходит по эндпойнту /items/search?text={text},
     * в text передаётся текст для поиска. Проверьте, что поиск возвращает только доступные для аренды вещи.
     */
    @GetMapping("search?text={text}")
    public Set<ItemDto> findItem(@RequestParam(value = "text") String text){
        return itemService.findItem(text);

    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItem(@PathVariable Long itemId){
        return itemService.deleteItem(itemId);
    }

}
