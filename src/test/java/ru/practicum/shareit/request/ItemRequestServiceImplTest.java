package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestForCreateDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestStorage mockItemRequestStorage;
    @Mock
    private ItemStorage mockItemStorage;
    @Mock
    private UserStorage mockUserStorage;
    private ItemRequestServiceImpl itemRequestService;
    private User user;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void set_up() {
        itemRequestService = new ItemRequestServiceImpl(mockItemRequestStorage, mockItemStorage, mockUserStorage);
    }

    @BeforeEach
    void createEntity() {
        user = new User();
        user.setId(100L);
        itemRequest = new ItemRequest();
        itemRequest.setId(100L);
        item = new Item();
        item.setRequestId(itemRequest);
    }

    @Test
    void createItemRequest() {
        when(mockUserStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            itemRequestService.createItemRequest(new ItemRequestForCreateDto(), 1L);
        });

        when(mockUserStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        when(mockItemRequestStorage.save(any()))
                .thenReturn(new ItemRequest());
        Assertions.assertEquals(ItemRequestMapper.toItemRequstDto(new ItemRequest()),
                itemRequestService.createItemRequest(new ItemRequestForCreateDto(), 1L));
    }

    @Test
    void findItemRequestForMe() {
        when(mockUserStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            itemRequestService.findItemRequestForMe(1L);
        });

        when(mockUserStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        when(mockItemRequestStorage.findByRequestor_IdOrderByCreatedDesc(any()))
                .thenReturn(List.of(itemRequest));
        when(mockItemStorage.findByRequestId_IdOrderByIdAsc(any()))
                .thenReturn(List.of(item));
        var rez = List.of(itemRequest).stream()
                .map(x -> ItemRequestMapper.toItemRequstDto(x,
                        List.of(item).stream()
                                .map(ItemMapper::toItemForRequestDto)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
        Assertions.assertEquals(rez, itemRequestService.findItemRequestForMe(1L));
    }

    @Test
    void findAllRequest() {
        when(mockItemRequestStorage.findAllByOrderByCreatedDesc())
                .thenReturn(List.of(itemRequest));
        when(mockItemStorage.findByRequestId_IdOrderByIdAsc(any()))
                .thenReturn(List.of(item));
        var rez = List.of(itemRequest).stream()
                .map(x -> ItemRequestMapper.toItemRequstDto(x,
                        List.of(item).stream()
                                .map(ItemMapper::toItemForRequestDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
        Assertions.assertEquals(rez, itemRequestService.findAllRequest(null, null, 100L));

        when(mockItemRequestStorage.findByRequestor_Id(any(), any()))
                .thenReturn(List.of(itemRequest));
        Assertions.assertEquals(rez, itemRequestService.findAllRequest(0, 1, 100L));
    }

    @Test
    void findById() {
        when(mockUserStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            itemRequestService.findById(1L, 1L);
        });

        when(mockUserStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Assertions.assertThrows(BadParametrException.class, () -> {
            itemRequestService.findById(0L, 1L);
        });

        when(mockItemRequestStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            itemRequestService.findById(1L, 1L);
        });

        when(mockItemRequestStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(mockItemStorage.findByRequestId_IdOrderByIdAsc(Mockito.anyLong()))
                .thenReturn(List.of(item));
        var rez = ItemRequestMapper.toItemRequstDto(itemRequest, List.of(item).stream()
                .map(ItemMapper::toItemForRequestDto)
                .collect(Collectors.toList()));
        Assertions.assertEquals(rez, itemRequestService.findById(1L, 1L));
    }
}