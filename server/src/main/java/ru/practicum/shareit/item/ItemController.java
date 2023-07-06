package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNextBookingAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


@RestController
@RequestMapping("/items")
@Validated
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(value = BookingController.HEADER_USER_ID_FIELD) Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @ResponseStatus(HttpStatus.OK) //для тестов постмана
    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody Comment comment, @PathVariable Long itemId,
                                    @RequestHeader(value = BookingController.HEADER_USER_ID_FIELD) Long userId) {
        return itemService.createComment(comment, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto patchItem(@RequestBody Item item,
                             @RequestHeader(value = BookingController.HEADER_USER_ID_FIELD) Long userId,
                             @PathVariable Long itemId) {
        return itemService.patchItem(item, userId, itemId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDtoLastNextBookingAndComments getItem(@PathVariable Long itemId,
                                                     @RequestHeader(value = BookingController.HEADER_USER_ID_FIELD) Long userId) {
        return itemService.getItemLastNextBookingAndComments(itemId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDtoLastNextBookingAndComments> getAllMyItems(
            @RequestHeader(value = BookingController.HEADER_USER_ID_FIELD) Long userId) {
        return itemService.getAllMyItems(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findItem(@RequestParam(value = "text") String text) {
        return itemService.findItem(text);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }

}
