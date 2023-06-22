package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForCreateDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestForCreateDto itemRequestForCreateDto, Long userId) {
        itemRequestForCreateDto.setCreated(LocalDateTime.now());
        var user = userStorage.findById(userId).orElseThrow(() -> {
            throw new NotFoundParametrException("указан несуществующий пользователь");
        });
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestForCreateDto, user);
        log.info("Создан запрос: {}", itemRequest);
        return ItemRequestMapper.toItemRequstDto(itemRequestStorage.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findItemRequestForMe(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundParametrException("указан несуществующий пользователь");
        }

        return itemRequestStorage.findByRequestor_IdOrderByCreatedDesc(userId).stream()
                .map(x -> ItemRequestMapper.toItemRequstDto(x,
                        itemStorage.findByRequestId(userId).stream()
                                .map(ItemMapper::toItemForRequestDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}













