package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForCreateDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestForCreateDto itemRequestForCreateDto, User user) {
        return new ItemRequest(null,
                itemRequestForCreateDto.getDescription(),
                user,
                itemRequestForCreateDto.getCreated());
    }

    public ItemRequestDto toItemRequstDto(ItemRequest itemRequest, List<ItemForRequestDto> itemForRequestDto){
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description((itemRequest.getDescription()))
                .created(itemRequest.getCreated())
                .items(itemForRequestDto)
                .build();
    }

    public ItemRequestDto toItemRequstDto(ItemRequest itemRequest){
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description((itemRequest.getDescription()))
                .created(itemRequest.getCreated())
                .items(new ArrayList<>())
                .build();
    }
}
