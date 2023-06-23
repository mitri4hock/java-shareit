package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForCreateDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestForCreateDto itemRequestForCreateDto, Long userId);

    List<ItemRequestDto> findItemRequestForMe(Long userId);

    List<ItemRequestDto> findAllRequest(Integer from, Integer size);

    ItemRequestDto findById(Long requestId);
}
