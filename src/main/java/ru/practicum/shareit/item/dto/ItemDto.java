package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    @Min(0)
    private Long id;
    private String name;
    private String description;
    @NotNull
    @Builder.Default
    private Boolean available = false;
    @NotNull
    private Long owner;
    private Long request;
}
