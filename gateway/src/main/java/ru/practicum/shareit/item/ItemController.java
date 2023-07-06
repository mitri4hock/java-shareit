package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestBody @Valid @NotNull ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        return itemClient.createItem(userId, itemDto);
    }

    @ResponseStatus(HttpStatus.OK) //для тестов постмана
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @Valid CommentDto comment,
                                                @PathVariable @NotNull Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemClient.createComment(userId, comment, itemId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> patchItem(@RequestBody @NotNull ItemDto item,
                                            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                            @PathVariable @NotNull Long itemId) {
        return itemClient.patchItem(itemId, userId, item);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemClient.getItemLastNextBookingAndComments(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllMyItems(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemClient.getAllMyItems(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findItem(@RequestParam(value = "text") String text) {
        return itemClient.findItem(text);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItem(@PathVariable Long itemId) {
        itemClient.deleteItem(itemId);
    }
}
