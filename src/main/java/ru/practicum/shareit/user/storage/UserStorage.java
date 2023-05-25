package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    UserDto createUser(UserDto userDto);

    Boolean isUsersEmailDuplicate(UserDto userDto);

    Optional<UserDto> getUserById(Long userId);

    UserDto patchUser(UserDto userDto, Long userId);

    Set<UserDto> getAllUsers();

    UserDto deleteUserById(Long userId);
}
