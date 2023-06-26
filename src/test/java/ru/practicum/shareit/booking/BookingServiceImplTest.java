package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    BookingStorage mockBookingStorage;
    @Mock
    UserStorage mockUserStorage;
    @Mock
    ItemStorage mockItemStorage;
    //@InjectMocks
    BookingServiceImpl bookingServiceImpl;




    @BeforeEach
    void createBookingServiceImpl() {
        bookingServiceImpl = new BookingServiceImpl(mockBookingStorage,
                mockUserStorage, mockItemStorage);
    }

    @Test
    void saveBooking_AllTest() {
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

    }


    @Test
    void findBookingById_AllTest() {
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
    void updateApproved_AllTest() {
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
        approved = true;
        booking.setStatus(EnumStatusBooking.APPROVED);

        Assertions.assertThrows(BadParametrException.class, () -> {
            bookingServiceImpl.updateApproved(bookingId, approved, userId);
        });


//        approved = false;
//
//        Assertions.assertThrows(BadParametrException.class, () -> {
//            bookingServiceImpl.updateApproved(bookingId, approved, userId);
//        });


    }

    @Test
    void findAllBookingWithStatus() {
    }

    @Test
    void countItemForUser() {
    }

    @Test
    void findAllBookingForUserWithStatus() {
    }
}