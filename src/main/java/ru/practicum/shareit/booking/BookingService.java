package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public BookingService(BookingStorage bookingStorage, UserStorage userStorage, ItemStorage itemStorage) {
        this.bookingStorage = bookingStorage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    public BookingDto saveBooking(BookingDtoForCreate bookingDtoCreate, Long userId) {
        if (bookingDtoCreate.getEnd().isBefore(bookingDtoCreate.getStart()) ||
                bookingDtoCreate.getEnd().equals(bookingDtoCreate.getStart())) {
            String str = String.format("Неверный формат даты старта и окончания бронирования: Start Date: %s " +
                            "End Date: %s Now: %s", bookingDtoCreate.getStart().toString(),
                    bookingDtoCreate.getEnd().toString(), LocalDateTime.now().toString());
            throw new BadParametrException(str);
        }

        Optional<Item> tempItem = itemStorage.findById(bookingDtoCreate.getItemId());
        if (tempItem.isEmpty()) {
            log.info("при бронировании, была указана несуществующая вещь");
            throw new NotFoundParametrException("при бронировании, была указана несуществующая вещь");
        }
        var item = tempItem.get();
        if (!item.getAvailable()) {
            throw new BadParametrException(String.format("при бронировании запрошена вещь со статусом " +
                    "Available = false. Item = %s", item.toString()));
        }
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundParametrException("при бронировании, был указан несуществующий пользователь владелец");
        }
        var ownerId = item.getOwner().getId();
        if (userId.equals(ownerId)) {
            throw new NotFoundParametrException("пользователь не может забронировать свою вещь");
        }
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item,
                userStorage.findById(userId).get());
        var rez = bookingStorage.save(booking);
        return BookingMapper.toBookingDto(rez);
    }

    public BookingDto findBookingById(Long id, Long userId) {
        var preRez = bookingStorage.findById(id);
        if (preRez.isEmpty()) {
            throw new NotFoundParametrException(String.format("отсутствует бронирование с id = %d", id));
        }
        if (preRez.get().getBooker().getId().equals(userId) ||
                preRez.get().getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingDto(preRez.get());
        } else {
            throw new NotFoundParametrException("запрашивать бронирование может или автор или владелец вещи");
        }
    }

    @Transactional
    public BookingDto updateApproved(Long bookingId, Boolean approved, Long userId) {
        var rez = bookingStorage.findById(bookingId);
        if (rez.isEmpty()) {
            throw new NotFoundParametrException(String.format("отсутствует бронирование с id = %d", bookingId));
        }
        if (!rez.get().getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundParametrException("Редактировать статус бронирования может только владелец вещи");
        }
        if (approved == true) {
            if (rez.get().getStatus() == EnumStatusBooking.APPROVED) {
                throw new BadParametrException("статус бронирование уже установлен на APPROVED");
            }
            rez.get().setStatus(EnumStatusBooking.APPROVED);
        } else {
            rez.get().setStatus(EnumStatusBooking.REJECTED);
        }
        return BookingMapper.toBookingDto(rez.get());
    }

    public List<BookingDto> findAllBookingWithStatus(Long userId, String state) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundParametrException(String.format("Запрошены бронирования от несуществующего пользователя." +
                    " Id = %d", userId));
        }
        List<Booking> preRez;
        switch (state) {
            case "ALL":
                preRez = bookingStorage.findByBooker_IdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                preRez = bookingStorage.findByBooker_IdAndStartBeforeAndEndAfterOrderByIdAsc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                preRez = bookingStorage.findByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                preRez = bookingStorage.findByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                preRez = bookingStorage.findByBooker_IdAndStatusOrderByStartDesc(userId, EnumStatusBooking.WAITING);
                break;
            case "REJECTED":
                preRez = bookingStorage.findByBooker_IdAndStatusOrderByStartDesc(userId, EnumStatusBooking.REJECTED);
                break;
            default:
                throw new BadParametrException("Unknown state: UNSUPPORTED_STATUS");
        }
        return preRez.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public long countItemForUser(Long userId) {
        return itemStorage.countDistinctByOwner_Id(userId);
    }

    public List<BookingDto> findAllBookingForUserWithStatus(Long userId, String state) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundParametrException(String.format("Запрошены бронирования от несуществующего пользователя." +
                    " Id = %d", userId));
        }
        if (countItemForUser(userId) == 0L) {
            return new ArrayList<>();
        }
        List<Booking> preRez;
        switch (state) {
            case "ALL":
                preRez = bookingStorage.findByItem_Owner_IdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                preRez = bookingStorage.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                preRez = bookingStorage.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                preRez = bookingStorage.findByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                preRez = bookingStorage.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, EnumStatusBooking.WAITING);
                break;
            case "REJECTED":
                preRez = bookingStorage.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, EnumStatusBooking.REJECTED);
                break;
            default:
                throw new BadParametrException("Unknown state: UNSUPPORTED_STATUS");
        }
        return preRez.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
