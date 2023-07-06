package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking", schema = "public")
@Data
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "start_date")
    private LocalDateTime start;
    @NotNull
    @Column(name = "end_date")
    private LocalDateTime end;
    @NotNull
    @ManyToOne()
    @JoinColumn(name = "item_id")
    private Item item;
    @NotNull
    @ManyToOne()
    @JoinColumn(name = "booker_id")
    private User booker;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnumStatusBooking status;

}
