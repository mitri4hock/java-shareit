package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestForCreateDto {
    private Long id;
    @NotNull
    private String description;
    private Long requestor;
    private LocalDateTime created;
}
