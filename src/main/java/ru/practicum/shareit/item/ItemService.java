package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingService;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {


    private ItemStorage itemStorage;
    private UserStorage userStorage;
    private BookingStorage bookingStorage;
    private CommentStorage commentStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage, BookingStorage bookingStorage,
                       CommentStorage commentStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.bookingStorage = bookingStorage;
        this.commentStorage = commentStorage;
    }

    public Optional<User> getUserById(Long userId) {
        Optional<User> rezQuery = userStorage.findById(userId);
        if (rezQuery.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(rezQuery.get());
    }

    public ItemDto createItem(Item item, Long userId) {
        if (getUserById(userId).isEmpty()) {
            throw new NotFoundParametrException("при создании вещи, был указан несуществующий пользователь владелец");
        }
        Item result = item;
        result.setOwner(userStorage.findById(userId).get());
        return ItemMapper.toItemDto(itemStorage.save(result));
    }

    public ItemDto patchItem(Item item, Long userId, Long itemId) {

        Optional<Item> tempItem = itemStorage.findById(itemId);
        if (tempItem.isEmpty()) {
            log.info("Попытка запросить редактирование отсутствующей вещи. itemId= {}", itemId);
            throw new BadParametrException("Отсутствует запрашиваемая вещь. itemId= " + itemId);
        }
        if (!userId.equals(tempItem.get().getOwner().getId())) {
            log.info("Попытка редактировать вещь не её владельцем. Полученный владелец: {}"
                    + " , текущий владелец: {}", userId, tempItem.get().getOwner());
            throw new NotFoundParametrException("Редактировать вещь может только её владелец. Полученный владелец: "
                    + userId + " , текущий владелец: " + tempItem.get().getOwner());
        }

        if (item.getName() != null) {
            tempItem.get().setName(item.getName());
            log.info("у вещи с id {} заменено название на {}", itemId, tempItem.get().getName());
        }
        if (item.getDescription() != null) {
            tempItem.get().setDescription(item.getDescription());
            log.info("у вещи с id {} заменено описание на {}", itemId, tempItem.get().getDescription());
        }
        if (item.getAvailable() != null) {
            tempItem.get().setAvailable(item.getAvailable());
            log.info("у вещи с id {} заменен статус на {}", itemId, tempItem.get().getAvailable());
        }


        return ItemMapper.toItemDto(itemStorage.save(tempItem.get()));
    }

    public Item getItem(Long itemId) {
        Optional<Item> tempItem = itemStorage.findById(itemId);
        if (tempItem.isEmpty()) {
            log.info("Попытка запросить отсутствующую вещь. itemId= {}", itemId);
            throw new NotFoundParametrException("Отсутствует запрашиваемая вещь. itemId= " + itemId);
        }
        return tempItem.get();
    }

    public List<ItemDtoLastNextBookingAndComments> getAllMyItems(Long userId) {
        return itemStorage.findByOwner_idOrderByIdAsc(userId).stream()
                .map(o -> ItemMapper.toItemDtoLastNextBookingAndComments(o,
                        BookingMapper.toBookingDtoSmallBooker(bookingStorage.findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(o.getId(), LocalDateTime.now(), EnumStatusBooking.APPROVED)),
                        BookingMapper.toBookingDtoSmallBooker(bookingStorage.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(o.getId(), LocalDateTime.now(), EnumStatusBooking.APPROVED)),
                        commentStorage.findByItem_Id(o.getId()).stream().map(CommentMapper::toCommentDto)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

    }

    public List<ItemDto> findItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        text = '%' + text + '%';
        var preRez = itemStorage.findByNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(text, text);
        return preRez.stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public void deleteItem(Long itemId) {
        itemStorage.deleteById(itemId);
    }

    public CommentDto createComment(Comment comment, Long itemId, Long userId) {
        var user = getUserById(userId);
        if (user.isEmpty()) {
            throw new NotFoundParametrException("при создании комментария, был указан несуществующий " +
                    "пользователь комментатор");
        }
        var item = getItem(itemId);
        if (item == null) {
            throw new NotFoundParametrException("при создании комментария, была указана несуществующая вещь");
        }
        var preRez = bookingStorage.findByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
        var booking = preRez.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        if (booking.size() < 1) {
            throw new BadParametrException("Пользователь не брал в аренду вещь. UserId = " + userId +
                    " ItemId = " + itemId);
        }
        comment.setItem(item);
        comment.setAuthor(user.get());
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentStorage.save(comment));
    }

    public Booking findLastBookingById(Long itemId) {
        return bookingStorage.findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(itemId, LocalDateTime.now(), EnumStatusBooking.APPROVED);
    }

    public Booking findNextBookingById(Long itemId) {
        return bookingStorage.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), EnumStatusBooking.APPROVED);
    }

    public List<Comment> findByItem_Id(Long itemId) {
        return commentStorage.findByItem_Id(itemId);
    }

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
