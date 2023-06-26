package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNextBookingAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    Optional<User> getUserById(Long userId);

    ItemDto createItem(ItemDto  itemDto, Long userId);

    ItemDto patchItem(Item item, Long userId, Long itemId);

    Item getItem(Long itemId);

    List<ItemDtoLastNextBookingAndComments> getAllMyItems(Long userId);

    List<ItemDto> findItem(String text);

    void deleteItem(Long itemId);

    CommentDto createComment(Comment comment, Long itemId, Long userId);

    Booking findLastBookingById(Long itemId);

    Booking findNextBookingById(Long itemId);

    List<Comment> findByItem_Id(Long itemId);

    ItemDtoLastNextBookingAndComments getItemLastNextBookingAndComments(Long itemId, Long userId);


}
