package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EnumStatusBooking;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    ItemStorage mockItemStorage;
    @Mock
    UserStorage mockUserStorage;
    @Mock
    BookingStorage mockBookingStorage;
    @Mock
    CommentStorage mockCommentStorage;
    @Mock
    ItemRequestStorage mockItemRequestStorage;

    ItemServiceImpl itemService;
    User user;
    Item item;

    @BeforeEach
    void createItemServiceImpl() {
        itemService = new ItemServiceImpl(mockItemStorage, mockUserStorage, mockBookingStorage,
                mockCommentStorage, mockItemRequestStorage);
    }

    @BeforeEach
    void createItem() {
        user = new User();
        user.setId(1L);
        item = new Item();
        item.setOwner(user);
    }


    @Test
    void getUserById_AllTest() {
        when(mockUserStorage.findById(any()))
                .thenReturn(Optional.empty());
        Assertions.assertEquals(Optional.empty(), itemService.getUserById(1L));

        when(mockUserStorage.findById(any()))
                .thenReturn(Optional.of(new User()));
        Assertions.assertEquals(Optional.of(new User()), itemService.getUserById(1L));
    }

    @Test
    void createItem_AllTest() {
        when(itemService.getUserById(any()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            itemService.createItem(ItemDto.builder().build(), 1L);
        });

        Item item = new Item();
        User user = new User();
        user.setId(1L);
        item.setOwner(user);
        ItemDto itemDto = ItemDto.builder()
                .owner(user.getId())
                .build();
        when(itemService.getUserById(any()))
                .thenReturn(Optional.of(new User()));
        when(mockUserStorage.findById(any()))
                .thenReturn(Optional.of(user));
        when(mockItemStorage.save(any()))
                .thenReturn(item);
        when(mockItemRequestStorage.findById(any()))
                .thenReturn(Optional.empty());
        itemDto.setRequestId(1L);
        Assertions.assertEquals(ItemMapper.toItemDto(item), itemService.createItem(itemDto,
                1L));
    }

    @Test
    void patchItem() {
        Long userId = 1L;
        Long itemId = 1L;
        Item item = new Item();
        User user = new User();
        user.setId(userId);
        item.setOwner(user);
        item.setName("testName");
        item.setDescription("testDescription");
        item.setAvailable(true);


        when(mockItemStorage.findById(any()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(BadParametrException.class, () -> {
            itemService.patchItem(item, userId, itemId);
        });

        when(mockItemStorage.findById(any()))
                .thenReturn(Optional.of(item));
        user.setId(2L);
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            itemService.patchItem(item, userId, itemId);
        });

        user.setId(userId);
        Assertions.assertEquals(ItemMapper.toItemDto(item), itemService.patchItem(item, userId, itemId));
    }

    @Test
    void getItem() {
        when(mockItemStorage.findById(any()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            itemService.getItem(1L);
        });

        when(mockItemStorage.findById(any()))
                .thenReturn(Optional.of(new Item()));
        Assertions.assertEquals(new Item(), itemService.getItem(1L));

    }

    @Test
    void getAllMyItems() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(user);
        when(mockItemStorage.findByOwner_idOrderByIdAsc(any()))
                .thenReturn(List.of(item));
        when(mockBookingStorage.findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(null);
        when(mockBookingStorage.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(null);
        when(mockCommentStorage.findByItem_Id(any()))
                .thenReturn(new ArrayList<>());

        var rez = List.of(item).stream()
                .map(o -> ItemMapper.toItemDtoLastNextBookingAndComments(o,
                        BookingMapper.toBookingDtoSmallBooker(
                                mockBookingStorage.findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(o.getId(),
                                        LocalDateTime.now(), EnumStatusBooking.APPROVED)),
                        BookingMapper.toBookingDtoSmallBooker(
                                mockBookingStorage.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(o.getId(),
                                        LocalDateTime.now(), EnumStatusBooking.APPROVED)),
                        mockCommentStorage.findByItem_Id(o.getId()).stream().map(CommentMapper::toCommentDto)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        Assertions.assertEquals(rez, itemService.getAllMyItems(1L));
    }

    @Test
    void findItem() {
        Assertions.assertEquals(new ArrayList<>(), itemService.findItem(""));

        Mockito
                .when(mockItemStorage.findByNameOrDescriptionLikeAndAvailableIsTrue(Mockito.anyString()))
                .thenReturn(List.of(item));
        Assertions.assertEquals(List.of(item).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()), itemService.findItem("asd"));
    }

    @Test
    void deleteItem() {
        itemService.deleteItem(1L);
        Mockito.verify(mockItemStorage, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void createComment() {
        when(itemService.getUserById(any()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            itemService.createComment(new Comment(), 1L, 1L);
        });

        when(itemService.getUserById(any()))
                .thenReturn(Optional.of(user));
        when(mockItemStorage.findById(any()))
                .thenReturn(Optional.of(new Item()));
        when(mockBookingStorage.findByBooker_IdAndEndBeforeOrderByStartDesc(any(), any()))
                .thenReturn(new ArrayList<>());
        Assertions.assertThrows(BadParametrException.class, () -> {
            itemService.createComment(new Comment(), 1L, 1L);
        });

        when(mockBookingStorage.findByBooker_IdAndEndBeforeOrderByStartDesc(any(), any()))
                .thenReturn(List.of(new Booking()));

        when(mockUserStorage.findById(any()))
                .thenReturn(Optional.of(user));
        when(mockItemStorage.findById(any()))
                .thenReturn(Optional.of(item));
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        when(mockCommentStorage.save(any()))
                .thenReturn(comment);
        Assertions.assertEquals(CommentMapper.toCommentDto(comment), itemService.createComment(new Comment(),
                1L, 1L));
    }

    @Test
    void findLastBookingById() {
        when(mockBookingStorage.findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(new Booking());
        Assertions.assertEquals(new Booking(), itemService.findLastBookingById(1L));
    }

    @Test
    void findNextBookingById() {
        when(mockBookingStorage.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(new Booking());
        Assertions.assertEquals(new Booking(), itemService.findNextBookingById(1L));
    }

    @Test
    void findByItem_Id() {
        when(mockCommentStorage.findByItem_Id(any()))
                .thenReturn(List.of(new Comment()));
        Assertions.assertEquals(List.of(new Comment()), itemService.findByItem_Id(1L));
    }

    @Test
    void getItemLastNextBookingAndComments() {
        Booking booking = new Booking();
        booking.setBooker(user);

        Comment comment = new Comment();
        comment.setAuthor(user);
        user.setName("TestNAme");

        when(mockItemStorage.findById(any()))
                .thenReturn(Optional.of(item));
        item.setId(1L);
        when(mockBookingStorage.findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(booking);
        when(mockBookingStorage.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(booking);
        when(mockCommentStorage.findByItem_Id(any()))
                .thenReturn(List.of(comment));

        var rez = ItemMapper.toItemDtoLastNextBookingAndComments(item,
                BookingMapper.toBookingDtoSmallBooker(booking),
                BookingMapper.toBookingDtoSmallBooker(booking),
                List.of(comment).stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()));

        Assertions.assertEquals(rez, itemService.getItemLastNextBookingAndComments(1L, 1L));


    }
}