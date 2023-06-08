package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.ConflictParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNextBookingAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemService itemService;
    private final BookingService bookingService;

    @Autowired
    ItemController(ItemService itemService,
                   BookingService bookingService) {
        this.itemService = itemService;
        this.bookingService = bookingService;
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

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody @Valid Comment comment, @PathVariable @NotNull Long itemId,
                                    @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {
        var user = itemService.getUserById(userId);
        if (user.isEmpty()) {
            throw new NotFoundParametrException("при создании комментария, был указан несуществующий " +
                    "пользователь комментатор");
        }
        var item = itemService.getItem(itemId);
        if (user == null) {
            throw new NotFoundParametrException("при создании комментария, была указана несуществующая вещь");
        }
        var booking = bookingService.findAllBookingWithStatus(userId, "PAST");
        if (booking.size() < 1) {
            throw new BadParametrException("Пользователь не брал в аренду вещь. UserId = " + userId +
                    " ItemId = " + itemId);
        }
        comment.setItem(item);
        comment.setAuthor(user.get());
        return itemService.createComment(comment);

    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody @NotNull Item item,
                             @RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId,
                             @PathVariable @NotNull Long itemId) {

        return itemService.patchItem(item, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoLastNextBookingAndComments getItem(@PathVariable Long itemId) {
        var item = itemService.getItem(itemId);
        var lastBooking = BookingMapper.toBookingDto(itemService.findLastBookingById(item.getId()));
        var nextBooking = BookingMapper.toBookingDto(itemService.findNextBookingById(item.getId()));
        var comments = itemService.findByItem_Id(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return ItemMapper.toItemDtoLastNextBookingAndComments(item, lastBooking, nextBooking, comments);
    }

    @GetMapping
    public List<ItemDtoLastNextBookingAndComments> getAllMyItems(@RequestHeader(value = "X-Sharer-User-Id") @NotNull Long userId) {
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
