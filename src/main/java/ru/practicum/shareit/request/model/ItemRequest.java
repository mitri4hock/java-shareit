package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "itemRequest", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description")
    @Size(max = 255)
    @NotBlank
    private String description;
    @ManyToOne()
    @JoinColumn(name = "requestor_id")
    private User requestor;
    @NotNull
    @Column(name = "created")
    private LocalDateTime created;
}
