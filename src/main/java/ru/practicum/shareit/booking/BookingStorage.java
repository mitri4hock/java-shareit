package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EnumStatusBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {

    Booking save(Booking booking);

    Optional<Booking> findById(Long id);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findByItem_Owner_Id(Long ownerId, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start,
                                                                               LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(Long id, LocalDateTime start, LocalDateTime end,
                                                               Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndEndBefore(Long id, LocalDateTime end, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start);

    List<Booking> findByItem_Owner_IdAndStartAfter(Long id, LocalDateTime start, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long id, EnumStatusBooking status);

    List<Booking> findByItem_Owner_IdAndStatus(Long id, EnumStatusBooking status, Pageable pageable);

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    Page<Booking> findByBooker_Id(Long bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByIdAsc(Long id, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfter(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end);

    List<Booking> findByBooker_IdAndEndBefore(Long id, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start);

    List<Booking> findByBooker_IdAndStartAfter(Long id, LocalDateTime start, Pageable pageable);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long id, EnumStatusBooking status);

    List<Booking> findByBooker_IdAndStatus(Long id, EnumStatusBooking status, Pageable pageable);

    Booking findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(Long id, LocalDateTime start, EnumStatusBooking status);

    Booking findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(Long id, LocalDateTime start, EnumStatusBooking status);


}
