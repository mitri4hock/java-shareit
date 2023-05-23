package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
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
        log.info("добавлен пользователь: " + rezultToReturn);
        return UserMapper.toUserDto(rezultToReturn);
    }

    @Override
    public Boolean isUsersEmailDuplicate(UserDto userDto) {
        return storage.values().stream()
                .anyMatch(x -> x.getEmail().equals(userDto.getEmail()) && !x.getId().equals(userDto.getId()))
                ;
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(storage.getOrDefault(userId, null));
    }

    @Override
    public UserDto patchUser(UserDto userDto, Long userId) {
        User patchingUser = storage.get(userId);
        if (userDto.getName() != null) {
            patchingUser.setName(userDto.getName());
            log.info("у пользователя с id " + userId + "заменено имя на " + patchingUser.getName());
        }
        if (userDto.getEmail() != null) {
            patchingUser.setEmail(userDto.getEmail());
            log.info("у пользователя с id " + userId + "заменено имя на " + patchingUser.getEmail());
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
