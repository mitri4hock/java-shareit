package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.ConflictParametrException;
import ru.practicum.shareit.user.dto.UserDto;

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
        if (userDto == null) {
            throw new BadParametrException("при создании пользователя передано пустое тело запроса");
        }
        if (userStorage.isUsersEmailDuplicate(userDto)) {
            throw new ConflictParametrException("при создании пользователя передан Email уже существующего пользователя");
        }
        return userStorage.createUser(userDto);
    }

    public UserDto patchUser(UserDto userDto, Long userId) {
        if (userDto == null || userId == null) {
            throw new BadParametrException("при обновлении вещи были переданы неверные параметры: " +
                    "user= " + userDto + " , userId= " + userId);
        }
        if (userStorage.getUserById(userId) == null) {
            log.info("Попытка запросить редактирование отсутствующего пользователя. userId= " + userId);
            throw new BadParametrException("Отсутствует запрашиваемый пользователь. userId= " + userId);
        }
        userDto.setId(userId);
        if (userStorage.isUsersEmailDuplicate(userDto)) {
            throw new ConflictParametrException("при обновлении пользователя передан Email уже существующего пользователя");
        }
        return userStorage.patchUser(userDto, userId);
    }

    public UserDto getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public Set<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public UserDto deleteUserById(Long userId) {
        return userStorage.deleteUserById(userId);
    }
}
