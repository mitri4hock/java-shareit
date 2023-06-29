package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingStorage mockBookingStorage;
    @Mock
    private UserStorage mockUserStorage;
    @Mock
    private ItemStorage mockItemStorage;
    private BookingServiceImpl bookingServiceImpl;

    @BeforeEach
    void createBookingServiceImpl() {
        bookingServiceImpl = new BookingServiceImpl(mockBookingStorage,
                mockUserStorage, mockItemStorage);
    }

    @Test
    void saveBookingAllTest() {
        Long userId = 1L;
        BookingDtoForCreate bookingDtoForCreate = new BookingDtoForCreate();
        bookingDtoForCreate.setEnd(LocalDateTime.now());
        bookingDtoForCreate.setStart(LocalDateTime.now());
        Assertions.assertThrows(BadParametrException.class, () -> {
            bookingServiceImpl.saveBooking(bookingDtoForCreate, userId);
        });

        bookingDtoForCreate.setEnd(LocalDateTime.now().plusDays(1L));
        bookingDtoForCreate.setItemId(3L);
        Mockito
                .when(mockItemStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            bookingServiceImpl.saveBooking(bookingDtoForCreate, userId);
        });

        Item item = new Item();
        item.setAvailable(false);
        Mockito
                .when(mockItemStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Assertions.assertThrows(BadParametrException.class, () -> {
            bookingServiceImpl.saveBooking(bookingDtoForCreate, userId);
        });

        item.setAvailable(true);
        Mockito
                .when(mockUserStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            bookingServiceImpl.saveBooking(bookingDtoForCreate, userId);
        });

        Mockito
                .when(mockUserStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        User user = new User();
        user.setId(userId);
        item.setOwner(user);
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            bookingServiceImpl.saveBooking(bookingDtoForCreate, userId);
        });

        user.setId(userId + 1);
        item.setOwner(user);
        Booking booking = BookingMapper.toBooking(bookingDtoForCreate, item,
                mockUserStorage.findById(userId).get());
        Mockito
                .when(mockBookingStorage.save(Mockito.any(Booking.class)))
                .thenReturn(booking);
        Assertions.assertEquals(BookingMapper.toBookingDto(booking),
                bookingServiceImpl.saveBooking(bookingDtoForCreate, userId));
        Mockito
                .when(mockBookingStorage.save(Mockito.any(Booking.class)))
                .thenReturn(null);
        Assertions.assertEquals(BookingMapper.toBookingDto(null),
                bookingServiceImpl.saveBooking(bookingDtoForCreate, userId));
    }


    @Test
    void findBookingByIdAllTest() {
        Long id = 1L;
        Long userId = 2L;

        Mockito
                .when(mockBookingStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            bookingServiceImpl.findBookingById(id, userId);
        });

        Booking booking = new Booking();
        User booker = new User();
        booker.setId(userId);
        booking.setBooker(booker);
        Item item = new Item();
        User booker2 = booker;
        item.setOwner(booker2);
        booking.setItem(item);
        Mockito
                .when(mockBookingStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Assertions.assertEquals(BookingMapper.toBookingDto(booking), bookingServiceImpl.findBookingById(id, userId));

        booker.setId(userId + 1L);
        booker2.setId(userId + 1L);
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            bookingServiceImpl.findBookingById(id, userId);
        });
    }

    @Test
    void updateApprovedAllTest() {
        Long bookingId = 1L;
        Boolean approved = true;
        Long userId = 2L;

        Mockito
                .when(mockBookingStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            bookingServiceImpl.updateApproved(bookingId, approved, userId);
        });

        Booking booking = new Booking();
        Item item = new Item();
        User owner = new User();
        item.setOwner(owner);
        booking.setItem(item);
        owner.setId(userId + 1);
        Mockito
                .when(mockBookingStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            bookingServiceImpl.updateApproved(bookingId, approved, userId);
        });

        owner.setId(userId);
        booking.setStatus(EnumStatusBooking.APPROVED);
        Assertions.assertThrows(BadParametrException.class, () -> {
            bookingServiceImpl.updateApproved(bookingId, true, userId);
        });

        booking.setStatus(EnumStatusBooking.WAITING);
        Assertions.assertEquals(EnumStatusBooking.APPROVED,
                bookingServiceImpl.updateApproved(bookingId, true, userId).getStatus());
        Assertions.assertEquals(EnumStatusBooking.REJECTED,
                bookingServiceImpl.updateApproved(bookingId, false, userId).getStatus());
    }

    @Test
    void findAllBookingWithStatusAllTest() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 1;

        Mockito
                .when(mockUserStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            bookingServiceImpl.findAllBookingWithStatus(userId, state, from, size);
        });

        Mockito
                .when(mockUserStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookingServiceImpl.findAllBookingWithStatus(userId, state, -1, size);
        });
        Assertions.assertThrows(ArithmeticException.class, () -> {
            bookingServiceImpl.findAllBookingWithStatus(userId, state, from, 0);
        });
        var testRez = List.of(new Booking()).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        Mockito
                .when(mockBookingStorage.findByBooker_IdOrderByStartDesc(Mockito.anyLong()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "ALL", null, null));

        Mockito
                .when(mockBookingStorage.findByBooker_IdAndStartBeforeAndEndAfterOrderByIdAsc(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "CURRENT", null, null));

        Mockito
                .when(mockBookingStorage.findByBooker_IdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                        Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "PAST", null, null));
        Mockito
                .when(mockBookingStorage.findByBooker_IdAndStartAfterOrderByStartDesc(Mockito.anyLong(),
                        Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "FUTURE", null, null));
        Mockito
                .when(mockBookingStorage.findByBooker_IdAndStatusOrderByStartDesc(Mockito.anyLong(),
                        Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "WAITING", null, null));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "REJECTED", null, null));
        Assertions.assertThrows(BadParametrException.class, () -> {
            bookingServiceImpl.findAllBookingWithStatus(userId, "ASDASDASD", null, null);
        });

        Mockito
                .when(mockBookingStorage.findByBooker_Id(Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl(List.of(new Booking())));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "ALL", 0, 1));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "ALL", 0, 2));

        Mockito
                .when(mockBookingStorage.findByBooker_IdAndStartBeforeAndEndAfter(Mockito.anyLong(),
                        Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "CURRENT", 0, 1));

        Mockito
                .when(mockBookingStorage.findByBooker_IdAndEndBefore(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "PAST", 0, 1));

        Mockito
                .when(mockBookingStorage.findByBooker_IdAndStartAfter(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "FUTURE", 0, 1));

        Mockito
                .when(mockBookingStorage.findByBooker_IdAndStatus(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "WAITING", 0, 1));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingWithStatus(userId, "REJECTED", 0, 1));
        Assertions.assertThrows(BadParametrException.class, () -> {
            bookingServiceImpl.findAllBookingWithStatus(userId, "ASDASDASD", 0, 1);
        });
    }

    @Test
    void countItemForUser() {
        Long rez = 1L;
        Mockito
                .when(mockItemStorage.countDistinctByOwner_Id(Mockito.anyLong()))
                .thenReturn(rez);
        Assertions.assertEquals(rez, bookingServiceImpl.countItemForUser(1L));
    }

    @Test
    void findAllBookingForUserWithStatus() {
        Long userId = 100L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 1;

        Mockito
                .when(mockUserStorage.findById(userId))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            bookingServiceImpl.findAllBookingForUserWithStatus(userId, state, from, size);
        });
        Mockito
                .when(mockUserStorage.findById(1L))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemStorage.countDistinctByOwner_Id(Mockito.anyLong()))
                .thenReturn(0L);
        Assertions.assertEquals(new ArrayList<>(),
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, state, 0, 1));

        Mockito
                .when(mockItemStorage.countDistinctByOwner_Id(Mockito.anyLong()))
                .thenReturn(1L);
        var testRez = List.of(new Booking()).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        Mockito
                .when(mockBookingStorage.findByItem_Owner_IdOrderByStartDesc(Mockito.anyLong()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, "ALL", null, null));

        Mockito
                .when(mockBookingStorage.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, "CURRENT", null, null));

        Mockito
                .when(mockBookingStorage.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                        Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, "PAST", null, null));
        Mockito
                .when(mockBookingStorage.findByItem_Owner_IdAndStartAfterOrderByStartDesc(Mockito.anyLong(),
                        Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, "FUTURE", null, null));
        Mockito
                .when(mockBookingStorage.findByItem_Owner_IdAndStatusOrderByStartDesc(Mockito.anyLong(),
                        Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, "WAITING", null, null));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, "REJECTED", null, null));
        Assertions.assertThrows(BadParametrException.class, () -> {
            bookingServiceImpl.findAllBookingForUserWithStatus(1L, "ASDASDASD", null, null);
        });

        Mockito
                .when(mockBookingStorage.findByItem_Owner_Id(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, "ALL", 0, 1));

        Mockito
                .when(mockBookingStorage.findByItem_Owner_IdAndStartBeforeAndEndAfter(Mockito.anyLong(),
                        Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, "CURRENT", 0, 1));

        Mockito
                .when(mockBookingStorage.findByItem_Owner_IdAndEndBefore(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, "PAST", 0, 1));
        Mockito
                .when(mockBookingStorage.findByItem_Owner_IdAndStartAfter(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, "FUTURE", 0, 1));
        Mockito
                .when(mockBookingStorage.findByItem_Owner_IdAndStatus(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, "WAITING", 0, 1));
        Assertions.assertEquals(testRez,
                bookingServiceImpl.findAllBookingForUserWithStatus(1L, "REJECTED", 0, 1));
        Assertions.assertThrows(BadParametrException.class, () -> {
            bookingServiceImpl.findAllBookingForUserWithStatus(1L, "ASDASDASD", 0, 1);
        });
    }
}