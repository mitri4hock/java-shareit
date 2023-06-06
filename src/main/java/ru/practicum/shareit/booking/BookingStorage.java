package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {

    Booking save(Booking booking);

    Optional<Booking> findById(Long id);



    @Query("Select *" +
            " from booking b left join item i on b.item_id = i.id " +
            " where i.id = ?1 " +
            " Order by b.start_date desc ")
    List<Booking> findAllBookingByItemOwner(Long ownerId);

    @Query("Select *" +
            " from booking b left join item i on b.item_id = i.id " +
            " where i.id = ?1 " +
            " and b.start_date < now() " +
            " and b.end_date > now() " +
            " Order by b.start_date desc ")
    List<Booking> findAllBookingByItemOwnerCurrent(Long ownerId);

    @Query("Select *" +
            " from booking b left join item i on b.item_id = i.id " +
            " where i.id = ?1 " +
            " and b.end_date < now() " +
            " Order by b.start_date desc ")
    List<Booking> findAllBookingByItemOwnerPast(Long bookerId);

    @Query("Select *" +
            " from booking b left join item i on b.item_id = i.id " +
            " where i.id = ?1 " +
            " and b.start_date > now() " +
            " Order by b.start_date desc ")
    List<Booking> findAllBookingByItemOwnerFuture(Long bookerId);

    @Query("Select *" +
            " from booking b left join item i on b.item_id = i.id " +
            " where i.id = ?1 " +
            " and b.status = 'WAITING' " +
            " Order by b.start_date desc ")
    List<Booking> findAllBookingByItemOwnerWaiting(Long bookerId);

    @Query("Select *" +
            " from booking b left join item i on b.item_id = i.id " +
            " where i.id = ?1 " +
            " and b.status = 'REJECTED' " +
            " Order by b.start_date desc ")
    List<Booking> findAllBookingByItemOwnerRejected(Long bookerId);

    @Query("Select *" +
            " from booking b " +
            " where booker_id = ?1 " +
            " Order by b.start_date desc ")
    List<Booking> findAllBookingByBooker(Long bookerId);

    @Query("Select *" +
            " from booking b " +
            " where booker_id = ?1 " +
            " and b.start_date < now() " +
            " and b.end_date > now() " +
            " Order by b.start_date desc ")
    List<Booking> findAllBookingByBookerCurrent(Long bookerId);

    @Query("Select *" +
            " from booking b " +
            " where booker_id = ?1 " +
            " and b.end_date < now() " +
            " Order by b.start_date desc ")
    List<Booking> findAllBookingByBookerPast(Long bookerId);

    @Query("Select *" +
            " from booking b " +
            " where booker_id = ?1 " +
            " and b.start_date > now() " +
            " Order by b.start_date desc ")
    List<Booking> findAllBookingByBookerFuture(Long bookerId);

    @Query("Select *" +
            " from booking b " +
            " where booker_id = ?1 " +
            " and b.status = 'WAITING' " +
            " Order by b.start_date desc ")
    List<Booking> findAllBookingByBookerWaiting(Long bookerId);

    @Query("Select *" +
            " from booking b " +
            " where booker_id = ?1 " +
            " and b.status = 'REJECTED' " +
            " Order by b.start_date desc ")
    List<Booking> findAllBookingByBookerRejected(Long bookerId);

}
