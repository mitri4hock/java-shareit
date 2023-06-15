package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(User user) {
        return UserMapper.toUserDto(userStorage.save(user));
    }

    @Transactional
    public UserDto patchUser(User user, Long userId) {
        User patchingUser = userStorage.findById(userId).get();
        if (user.getName() != null) {
            patchingUser.setName(user.getName());
            log.info("у пользователя с id {} заменено имя на {}", userId, patchingUser.getName());
        }
        if (user.getEmail() != null) {
            patchingUser.setEmail(user.getEmail());
            log.info("у пользователя с id {} заменено имя на {}", userId, patchingUser.getEmail());
        }

        return UserMapper.toUserDto(patchingUser);
    }

    public UserDto getUserById(Long userId) {
        var rez = userStorage.findById(userId);
        if (rez.isEmpty()) {
            throw new NotFoundParametrException(String.format("Отсутствует запрашиваемый пользователь. userId= %d",
                    userId));
        }
        return UserMapper.toUserDto(rez.get());
    }

    public List<UserDto> getAllUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void deleteUserById(Long userId) {
        userStorage.deleteById(userId);
    }

    private List<User> isUsersEmailDuplicate(User user) {
        return userStorage.findByEmailContainingIgnoreCase(user.getEmail());
    }

}
