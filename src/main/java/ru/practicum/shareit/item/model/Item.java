package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "item", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(name = "name")
    @Size(max = 255)
    private String name;
    @NotBlank
    @Column(name = "description")
    @Size(max = 255)
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
