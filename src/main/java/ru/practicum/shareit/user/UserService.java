package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictParametrException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(UserDto userDto) {
        if (userStorage.isUsersEmailDuplicate(userDto)) {
            throw new ConflictParametrException("при создании пользователя передан Email уже существующего пользователя");
        }
        return userStorage.createUser(userDto);
    }

    public UserDto patchUser(UserDto userDto, Long userId) {
        userDto.setId(userId);
        if (userStorage.isUsersEmailDuplicate(userDto)) {
            throw new ConflictParametrException("при обновлении пользователя передан Email уже существующего пользователя");
        }
        return userStorage.patchUser(userDto, userId);
    }

    public Optional<UserDto> getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public Set<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public UserDto deleteUserById(Long userId) {
        return userStorage.deleteUserById(userId);
    }
}
