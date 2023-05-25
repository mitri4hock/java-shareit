package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserStorageInMemoryImpl implements UserStorage {
    private final Map<Long, User> storage;
    private Long currentUserId;

    @Autowired
    UserStorageInMemoryImpl() {
        this.storage = new HashMap<>();
        currentUserId = 1L;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        storage.put(currentUserId, UserMapper.toUser(userDto, currentUserId));
        currentUserId++;
        User rezultToReturn = storage.get(currentUserId - 1);
        log.info("добавлен пользователь: {}", rezultToReturn);
        return UserMapper.toUserDto(rezultToReturn);
    }

    @Override
    public Boolean isUsersEmailDuplicate(UserDto userDto) {
        return storage.values().stream()
                .anyMatch(x -> x.getEmail().equals(userDto.getEmail()) && !x.getId().equals(userDto.getId()))
                ;
    }

    @Override
    public Optional<UserDto> getUserById(Long userId) {
        var preRez = storage.getOrDefault(userId, null);
        if (preRez == null) {
            return Optional.empty();
        }
        return Optional.of(UserMapper.toUserDto(preRez));
    }

    @Override
    public UserDto patchUser(UserDto userDto, Long userId) {
        User patchingUser = storage.get(userId);
        if (userDto.getName() != null) {
            patchingUser.setName(userDto.getName());
            log.info("у пользователя с id {} заменено имя на {}", userId, patchingUser.getName());
        }
        if (userDto.getEmail() != null) {
            patchingUser.setEmail(userDto.getEmail());
            log.info("у пользователя с id {} заменено имя на {}", userId, patchingUser.getEmail());
        }
        return UserMapper.toUserDto(patchingUser);
    }

    @Override
    public Set<UserDto> getAllUsers() {
        return storage.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toSet());
    }

    @Override
    public UserDto deleteUserById(Long userId) {
        UserDto tempUser = UserMapper.toUserDto(storage.getOrDefault(userId, null));
        storage.remove(userId);
        return tempUser;
    }
}
