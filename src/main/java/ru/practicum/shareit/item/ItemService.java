package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNextBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserStorage;

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

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage, BookingStorage bookingStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.bookingStorage = bookingStorage;
    }

    public Optional<UserDto> getUserById(Long userId) {
        Optional<User> rezQuery = userStorage.findById(userId);
        if (rezQuery.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(UserMapper.toUserDto(rezQuery.get()));
    }

    public ItemDto createItem(Item item, Long userId) {
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

    public ItemDto getItem(Long itemId) {
        Optional<Item> tempItem = itemStorage.findById(itemId);
        if (tempItem.isEmpty()) {
            log.info("Попытка запросить отсутствующую вещь. itemId= {}", itemId);
            throw new NotFoundParametrException("Отсутствует запрашиваемая вещь. itemId= " + itemId);
        }
        return ItemMapper.toItemDto(tempItem.get());
    }

    public List<ItemDtoLastNextBooking> getAllMyItems(Long userId) {
        return itemStorage.findByOwner_id(userId).stream()
                .map(o -> ItemMapper.toItemDtoLastNextBooking(o,
                        BookingMapper.toBookingDto(bookingStorage.findLastBookingById(o.getId())),
                        BookingMapper.toBookingDto(bookingStorage.findLastBookingById(o.getId()))))
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
}
