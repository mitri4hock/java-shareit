package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictParametrException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserStorage userStorage;



    public UserDto createUser(User user) {
        if (isUsersEmailDuplicate(user)) {
            throw new ConflictParametrException("при создании пользователя передан Email уже существующего пользователя");
        }
        return UserMapper.toUserDto(userStorage.save(user));
    }

    public UserDto patchUser(UserDto userDto, Long userId) {
        userDto.setId(userId);
        if (isUsersEmailDuplicate(UserMapper.toUser(userDto, userId))) {
            throw new ConflictParametrException("при обновлении пользователя передан Email уже существующего пользователя");
        }

        User patchingUser = userStorage.findById(userId).get();
        if (userDto.getName() != null) {
            patchingUser.setName(userDto.getName());
            log.info("у пользователя с id {} заменено имя на {}", userId, patchingUser.getName());
        }
        if (userDto.getEmail() != null) {
            patchingUser.setEmail(userDto.getEmail());
            log.info("у пользователя с id {} заменено имя на {}", userId, patchingUser.getEmail());
        }
        userStorage.save(patchingUser);

        return UserMapper.toUserDto(patchingUser);
    }

    public Optional<UserDto> getUserById(Long userId) {
        Optional<User> rezQuery = userStorage.findById(userId);
        if (rezQuery.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(UserMapper.toUserDto(rezQuery.get()));
    }

    public List<UserDto> getAllUsers() {
        return userStorage.findAll().stream().
                map(UserMapper::toUserDto).
                collect(Collectors.toList());
    }

    public void deleteUserById(Long userId) {
        userStorage.deleteById(userId);
    }

    private boolean isUsersEmailDuplicate(User user) {
        return userStorage.findByEmailContainingIgnoreCase(user.getEmail()).size() != 0;
    }
}
