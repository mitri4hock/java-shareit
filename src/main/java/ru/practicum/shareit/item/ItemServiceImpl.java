package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDtoSmallBooker;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNextBookingAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;

    @Override
    public Optional<User> getUserById(Long userId) {
        Optional<User> rezQuery = userStorage.findById(userId);
        if (rezQuery.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(rezQuery.get());
    }

    @Override
    @Transactional
    public ItemDto createItem(Item item, Long userId) {
        if (getUserById(userId).isEmpty()) {
            throw new NotFoundParametrException("при создании вещи, был указан несуществующий пользователь владелец");
        }
        item.setOwner(userStorage.findById(userId).get());
        var rez = itemStorage.save(item);
        return ItemMapper.toItemDto(rez);
    }

    @Override
    @Transactional
    public ItemDto patchItem(Item item, Long userId, Long itemId) {
        Item tempItem = itemStorage.findById(itemId).orElseThrow(() -> {
            log.info("Попытка запросить редактирование отсутствующей вещи. itemId= {}", itemId);
            throw new BadParametrException(String.format("Отсутствует запрашиваемая вещь. itemId= %d", itemId));
        });
        if (!userId.equals(tempItem.getOwner().getId())) {
            log.info("Попытка редактировать вещь не её владельцем. Полученный владелец: {}"
                    + " , текущий владелец: {}", userId, tempItem.getOwner());
            throw new NotFoundParametrException(String.format("Редактировать вещь может только её владелец." +
                    " Полученный владелец: %d, текущий владелец: %s", userId, tempItem.getOwner().toString()));
        }
        if (item.getName() != null) {
            tempItem.setName(item.getName());
            log.info("у вещи с id {} заменено название на {}", itemId, tempItem.getName());
        }
        if (item.getDescription() != null) {
            tempItem.setDescription(item.getDescription());
            log.info("у вещи с id {} заменено описание на {}", itemId, tempItem.getDescription());
        }
        if (item.getAvailable() != null) {
            tempItem.setAvailable(item.getAvailable());
            log.info("у вещи с id {} заменен статус на {}", itemId, tempItem.getAvailable());
        }
        return ItemMapper.toItemDto(tempItem);
    }

    @Override
    public Item getItem(Long itemId) {
        Item tempItem = itemStorage.findById(itemId).orElseThrow(() -> {
            log.info("Попытка запросить отсутствующую вещь. itemId= {}", itemId);
            throw new NotFoundParametrException(String.format("Отсутствует запрашиваемая вещь. itemId= %s",
                    itemId.toString()));
        });

        return tempItem;
    }

    @Override
    public List<ItemDtoLastNextBookingAndComments> getAllMyItems(Long userId) {
        return itemStorage.findByOwner_idOrderByIdAsc(userId).stream()
                .map(o -> ItemMapper.toItemDtoLastNextBookingAndComments(o,
                        BookingMapper.toBookingDtoSmallBooker(
                                bookingStorage.findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(o.getId(),
                                        LocalDateTime.now(), EnumStatusBooking.APPROVED)),
                        BookingMapper.toBookingDtoSmallBooker(
                                bookingStorage.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(o.getId(),
                                        LocalDateTime.now(), EnumStatusBooking.APPROVED)),
                        commentStorage.findByItem_Id(o.getId()).stream().map(CommentMapper::toCommentDto)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

    }

    @Override
    public List<ItemDto> findItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        text = '%' + text + '%';
        var preRez = itemStorage.findByNameOrDescriptionLikeAndAvailableIsTrue(text);
        return preRez.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        itemStorage.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentDto createComment(Comment comment, Long itemId, Long userId) {
        var user = getUserById(userId).orElseThrow(() -> {
            throw new NotFoundParametrException("при создании комментария, был указан несуществующий " +
                    "пользователь комментатор");
        });
        var item = getItem(itemId);
        if (item == null) {
            throw new NotFoundParametrException("при создании комментария, была указана несуществующая вещь");
        }
        var preRez = bookingStorage.findByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
        var booking = preRez.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        if (booking.size() < 1) {
            throw new BadParametrException(String.format("Пользователь не брал в аренду вещь. UserId = %d ItemId = %d",
                    userId, itemId));
        }
        comment.setItem(item);
        comment.setAuthor(user);
        return CommentMapper.toCommentDto(commentStorage.save(comment));
    }

    @Override
    public Booking findLastBookingById(Long itemId) {
        return bookingStorage.findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(itemId, LocalDateTime.now(),
                EnumStatusBooking.APPROVED);
    }

    @Override
    public Booking findNextBookingById(Long itemId) {
        return bookingStorage.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(),
                EnumStatusBooking.APPROVED);
    }

    @Override
    public List<Comment> findByItem_Id(Long itemId) {
        return commentStorage.findByItem_Id(itemId);
    }

    @Override
    public ItemDtoLastNextBookingAndComments getItemLastNextBookingAndComments(Long itemId, Long userId) {
        var item = getItem(itemId);
        BookingDtoSmallBooker lastBooking = null;
        BookingDtoSmallBooker nextBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = BookingMapper.toBookingDtoSmallBooker(findLastBookingById(item.getId()));
            nextBooking = BookingMapper.toBookingDtoSmallBooker(findNextBookingById(item.getId()));
        }
        var comments = findByItem_Id(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return ItemMapper.toItemDtoLastNextBookingAndComments(item, lastBooking, nextBooking, comments);
    }
}
