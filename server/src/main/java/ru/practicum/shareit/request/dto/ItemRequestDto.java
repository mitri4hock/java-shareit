package ru.practicum.shareit.request.dto;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    @NotNull
    private LocalDateTime created;
    @NotNull
    private List<ItemForRequestDto> items;
}
