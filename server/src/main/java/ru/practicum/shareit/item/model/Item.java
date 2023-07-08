package ru.practicum.shareit.item.model;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "item", schema = "public")
@Data
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @NotNull
    @Column(name = "available")
    private Boolean available;
    @ManyToOne()
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne()
    @JoinColumn(name = "request_id")
    private ItemRequest requestId;
}
