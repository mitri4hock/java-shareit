package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.awt.print.Book;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {

Booking save(Booking booking);

}
