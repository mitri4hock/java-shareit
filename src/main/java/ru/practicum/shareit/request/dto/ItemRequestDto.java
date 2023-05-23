package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Data
@Builder
public class ItemRequestDto {
    @Min(0)
    private Long id;
    private String description;
    @Min(0)
    private Long requestor;
    @NotNull
    private Date created;
}
