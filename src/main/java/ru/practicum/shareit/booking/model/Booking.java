package ru.practicum.shareit.booking.model;

import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "booking", schema = "public")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant start;
    @NotNull
    @Column(name = "end_date", nullable = false)
    private Instant end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item itemId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnumStatusBooking status;

}
