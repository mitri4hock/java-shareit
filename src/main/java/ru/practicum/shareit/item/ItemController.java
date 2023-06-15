package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNextBookingAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequestMapping("/items")
@Validated
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody @Valid @NotNull Item item,
                              @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.createItem(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody @Valid Comment comment, @PathVariable @NotNull Long itemId,
                                    @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.createComment(comment, itemId, userId);

    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody @NotNull Item item,
                             @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                             @PathVariable @NotNull Long itemId) {
        return itemService.patchItem(item, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoLastNextBookingAndComments getItem(@PathVariable Long itemId,
                                                     @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.getItemLastNextBookingAndComments(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoLastNextBookingAndComments> getAllMyItems(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.getAllMyItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItem(@RequestParam(value = "text") String text) {
        return itemService.findItem(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }

}
