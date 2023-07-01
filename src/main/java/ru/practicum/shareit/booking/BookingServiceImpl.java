package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EnumState;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    @Transactional
    public BookingDto saveBooking(BookingDtoForCreate bookingDtoCreate, Long userId) {
        if (bookingDtoCreate.getEnd().isBefore(bookingDtoCreate.getStart()) ||
                bookingDtoCreate.getEnd().equals(bookingDtoCreate.getStart())) {
            String str = String.format("Неверный формат даты старта и окончания бронирования: Start Date: %s " +
                            "End Date: %s Now: %s", bookingDtoCreate.getStart().toString(),
                    bookingDtoCreate.getEnd().toString(), LocalDateTime.now().toString());
            throw new BadParametrException(str);
        }

        Item item = itemStorage.findById(bookingDtoCreate.getItemId()).orElseThrow(() -> {
            log.info("при бронировании, была указана несуществующая вещь");
            throw new NotFoundParametrException("при бронировании, была указана несуществующая вещь");
        });
        if (!item.getAvailable()) {
            log.info("при бронировании запрошена вещь со статусом Available = false. Item = {}", item);
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

    @Override
    public BookingDto findBookingById(Long id, Long userId) {
        var preRez = bookingStorage.findById(id).orElseThrow(() -> {
            throw new NotFoundParametrException(String.format("отсутствует бронирование с id = %d", id));
        });
        if (preRez.getBooker().getId().equals(userId) ||
                preRez.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingDto(preRez);
        } else {
            throw new NotFoundParametrException("запрашивать бронирование может или автор или владелец вещи");
        }
    }

    @Override
    @Transactional
    public BookingDto updateApproved(Long bookingId, Boolean approved, Long userId) {
        var rez = bookingStorage.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundParametrException(String.format("отсутствует бронирование с id = %d", bookingId));
        });
        if (!rez.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundParametrException("Редактировать статус бронирования может только владелец вещи");
        }
        if (approved == true) {
            if (rez.getStatus() == EnumStatusBooking.APPROVED) {
                throw new BadParametrException("статус бронирование уже установлен на APPROVED");
            }
            rez.setStatus(EnumStatusBooking.APPROVED);
        } else {
            rez.setStatus(EnumStatusBooking.REJECTED);
        }
        return BookingMapper.toBookingDto(rez);
    }

    @Override
    public List<BookingDto> findAllBookingWithStatus(Long userId, String state, Integer from, Integer size) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundParametrException(String.format("Запрошены бронирования от несуществующего пользователя." +
                    " Id = %d", userId));
        }
        List<Booking> preRez = new ArrayList<>();
        EnumState stateEnum;
        try {
            stateEnum = EnumState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadParametrException("Unknown state: UNSUPPORTED_STATUS");
        }

        if (from == null && size == null) {
            switch (stateEnum) {
                case ALL:
                    preRez = bookingStorage.findByBooker_IdOrderByStartDesc(userId);
                    break;
                case CURRENT:
                    preRez = bookingStorage.findByBooker_IdAndStartBeforeAndEndAfterOrderByIdAsc(userId,
                            LocalDateTime.now(), LocalDateTime.now());
                    break;
                case PAST:
                    preRez = bookingStorage.findByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                    break;
                case FUTURE:
                    preRez = bookingStorage.findByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                    break;
                case WAITING:
                    preRez = bookingStorage.findByBooker_IdAndStatusOrderByStartDesc(userId, EnumStatusBooking.WAITING);
                    break;
                case REJECTED:
                    preRez = bookingStorage.findByBooker_IdAndStatusOrderByStartDesc(userId, EnumStatusBooking.REJECTED);
                    break;
            }
        } else {
            switch (stateEnum) {
                case ALL:
                    Sort sortBy = Sort.by(Sort.Order.desc("start"));
                    Pageable page = new CustomPageRequest(from, size, sortBy);
                    Page<Booking> prePreRez = bookingStorage.findByBooker_Id(userId, page);
                    preRez = prePreRez.getContent();
                    break;
                case CURRENT:
                    Sort sortByCurrent = Sort.by(Sort.Order.asc("id"));
                    Pageable pageCurrent = new CustomPageRequest(from, size, sortByCurrent);
                    preRez = bookingStorage.findByBooker_IdAndStartBeforeAndEndAfter(userId,
                            LocalDateTime.now(), LocalDateTime.now(), pageCurrent);
                    break;
                case PAST:
                    Sort sortByPast = Sort.by(Sort.Order.desc("start"));
                    Pageable pagePast = new CustomPageRequest(from, size, sortByPast);
                    preRez = bookingStorage.findByBooker_IdAndEndBefore(userId, LocalDateTime.now(), pagePast);
                    break;
                case FUTURE:
                    Sort sortByFuture = Sort.by(Sort.Order.desc("start"));
                    Pageable pageFuture = new CustomPageRequest(from, size, sortByFuture);
                    preRez = bookingStorage.findByBooker_IdAndStartAfter(userId, LocalDateTime.now(), pageFuture);
                    break;
                case WAITING:
                    Sort sortByWaiting = Sort.by(Sort.Order.desc("start"));
                    Pageable pageWaiting = new CustomPageRequest(from, size, sortByWaiting);
                    preRez = bookingStorage.findByBooker_IdAndStatus(userId, EnumStatusBooking.WAITING, pageWaiting);
                    break;
                case REJECTED:
                    Sort sortByRejected = Sort.by(Sort.Order.desc("start"));
                    Pageable pageRejected = new CustomPageRequest(from, size, sortByRejected);
                    preRez = bookingStorage.findByBooker_IdAndStatus(userId, EnumStatusBooking.REJECTED, pageRejected);
                    break;
            }
        }
        return preRez.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public long countItemForUser(Long userId) {
        return itemStorage.countDistinctByOwner_Id(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllBookingForUserWithStatus(Long userId, String state, Integer from, Integer size) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundParametrException(String.format("Запрошены бронирования от несуществующего пользователя." +
                    " Id = %d", userId));
        }
        if (countItemForUser(userId) == 0L) {
            return new ArrayList<>();
        }
        List<Booking> preRez = new ArrayList<>();
        EnumState stateEnum;
        try {
            stateEnum = EnumState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadParametrException("Unknown state: UNSUPPORTED_STATUS");
        }
        if (from == null && size == null) {
            switch (stateEnum) {
                case ALL:
                    preRez = bookingStorage.findByItem_Owner_IdOrderByStartDesc(userId);
                    break;
                case CURRENT:
                    preRez = bookingStorage.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                            LocalDateTime.now(), LocalDateTime.now());
                    break;
                case PAST:
                    preRez = bookingStorage.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                    break;
                case FUTURE:
                    preRez = bookingStorage.findByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                    break;
                case WAITING:
                    preRez = bookingStorage.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, EnumStatusBooking.WAITING);
                    break;
                case REJECTED:
                    preRez = bookingStorage.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, EnumStatusBooking.REJECTED);
                    break;
            }
        } else {
            Sort sortByStart = Sort.by(Sort.Order.desc("start"));
            Pageable pageStart = new CustomPageRequest(from, size, sortByStart);

            switch (stateEnum) {
                case ALL:
                    preRez = bookingStorage.findByItem_Owner_Id(userId, pageStart);
                    break;
                case CURRENT:
                    preRez = bookingStorage.findByItem_Owner_IdAndStartBeforeAndEndAfter(userId,
                            LocalDateTime.now(), LocalDateTime.now(), pageStart);
                    break;
                case PAST:
                    preRez = bookingStorage.findByItem_Owner_IdAndEndBefore(userId, LocalDateTime.now(), pageStart);
                    break;
                case FUTURE:
                    preRez = bookingStorage.findByItem_Owner_IdAndStartAfter(userId, LocalDateTime.now(), pageStart);
                    break;
                case WAITING:
                    preRez = bookingStorage.findByItem_Owner_IdAndStatus(userId, EnumStatusBooking.WAITING, pageStart);
                    break;
                case REJECTED:
                    preRez = bookingStorage.findByItem_Owner_IdAndStatus(userId, EnumStatusBooking.REJECTED, pageStart);
                    break;
            }
        }
        return preRez.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
