package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ItemRequestForCreateDto {
    private Long id;
    @NotNull
    private String description;
    private Long requestor;
    private LocalDateTime created;
}
