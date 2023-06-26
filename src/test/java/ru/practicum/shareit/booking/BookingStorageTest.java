package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingStorageTest {
    @Autowired
    BookingStorage bookingStorage;
    @Autowired
    UserStorage userStorage;
    @Autowired
    ItemStorage itemStorage;

    @Test
    void testOne(){
        User user = new User();
        user.setEmail("asd@sd.ru");
        user.setName("asd");
        userStorage.save(user);
        Item item = new Item();
        item.setName("asd");
        item.setDescription("sdf");
        item.setAvailable(true);
        item.setOwner(user);
        itemStorage.save(item);


        Booking booking = new Booking();
        Assertions.assertNull(booking.getId());
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(EnumStatusBooking.REJECTED);
        bookingStorage.save(booking);
        Assertions.assertNotNull(booking.getId());
    }

}