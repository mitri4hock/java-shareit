package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ItemRequestForCreateDto {
    private Long id;
    @NotBlank
    private String description;
    private Long requestor;
    private LocalDateTime created;
}
