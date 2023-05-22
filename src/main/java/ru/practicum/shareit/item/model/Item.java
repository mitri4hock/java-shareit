package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class Item {
    @Min(0)
    private Long id;
    private String name;
    private String description;
    @NotNull
    private Boolean available;
    @NotNull
    private Long owner;
    private Long request;
}
