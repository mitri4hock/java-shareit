package ru.practicum.shareit.request.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "itemRequest", schema = "public")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description")
    private String description;
    @Column(name = "requestor_id", nullable = false)
    private Long requestor;
    @NotNull
    @Column(name = "created", nullable = false)
    private Date created;
}
