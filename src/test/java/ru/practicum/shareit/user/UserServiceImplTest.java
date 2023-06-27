package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserStorage mockUserStorage;

    UserServiceImpl userService;
    User user;

    @BeforeEach
    void set_up() {
        userService = new UserServiceImpl(mockUserStorage);
    }

    @BeforeEach
    void createuser() {
        user = new User();
        user.setName("testName");
        user.setEmail("test@test.test");
    }

    @Test
    void createUser() {
        when(mockUserStorage.save(any()))
                .thenReturn(new User());
        Assertions.assertEquals(UserMapper.toUserDto(new User()), userService.createUser(new User()));

    }

    @Test
    void patchUser() {
        when(mockUserStorage.findById(any()))
                .thenReturn(Optional.of(user));
        Assertions.assertEquals(UserMapper.toUserDto(user), userService.patchUser(user, 100L));
    }

    @Test
    void getUserById() {
        when(mockUserStorage.findById(any()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundParametrException.class, () -> {
            userService.getUserById(100L);
        });

        when(mockUserStorage.findById(any()))
                .thenReturn(Optional.of(user));
        Assertions.assertEquals(UserMapper.toUserDto(user), userService.getUserById(100L));
    }

    @Test
    void getAllUsers() {
        when(mockUserStorage.findAll())
                .thenReturn(List.of(user));

        var rez = List.of(user).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(rez, userService.getAllUsers());
    }

    @Test
    void deleteUserById() {

        userService.deleteUserById(1L);
        Mockito.verify(mockUserStorage, Mockito.times(1)).deleteById(1L);

    }
}