package ru.practicum.shareit.request.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "itemRequest", schema = "public")
@Data
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description")
    @Size(max = 255)
    private String description;
    @Column(name = "requestor_id")
    private Long requestor;
    @NotNull
    @Column(name = "created")
    private Date created;
}
