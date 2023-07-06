package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class ItemRequestDto {
    @Min(0)
    private Long id;
    private String description;
    @NotNull
    private LocalDateTime created;
    @NotNull
    private List<ItemForRequestDto> items;
}
