package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadParametrException;
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
                        itemStorage.findByRequestId_IdOrderByIdAsc(x.getId()).stream()
                                .map(ItemMapper::toItemForRequestDto)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findAllRequest(Integer from, Integer size, Long userId) {
        if (from == null && size == null) {
            return itemRequestStorage.findAllByOrderByCreatedDesc().stream()
                    .map(x -> ItemRequestMapper.toItemRequstDto(x,
                            itemStorage.findByRequestId_IdOrderByIdAsc(x.getId()).stream()
                                    .map(ItemMapper::toItemForRequestDto)
                                    .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        }
        if (from < 0 || size < 1) {
            throw new BadParametrException(String.format("При запросе ItemRequest были переданы неверные параметры: " +
                    "from: %d, size: %d", from, size));
        }
        Sort sortById = Sort.by(Sort.Order.desc("created"));
        Pageable page = PageRequest.of(from, size, sortById);
        var itemRequestPage = itemRequestStorage.findByRequestor_Id(userId, page);
        return itemRequestPage.stream()
                .map(x -> ItemRequestMapper.toItemRequstDto(x,
                        itemStorage.findByRequestId_IdOrderByIdAsc(x.getId()).stream()
                                .map(ItemMapper::toItemForRequestDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto findById(Long requestId, Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundParametrException("указан несуществующий пользователь");
        }
        if (requestId < 1) {
            throw new BadParametrException(String.format("При запросе ItemRequest был передан неверный id запроса: " +
                    "id = %d", requestId));
        }
        var request = itemRequestStorage.findById(requestId).orElseThrow(() -> {
            throw new NotFoundParametrException(String.format("не был найден запрос с id = %d", requestId));
        });
        return ItemRequestMapper.toItemRequstDto(request, itemStorage.findByRequestId_IdOrderByIdAsc(request.getId()).stream()
                .map(ItemMapper::toItemForRequestDto)
                .collect(Collectors.toList()));
    }
}








































