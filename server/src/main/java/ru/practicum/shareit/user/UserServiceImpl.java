package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    @Transactional
    public UserDto createUser(User user) {
        return UserMapper.toUserDto(userStorage.save(user));
    }

    @Override
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

    @Override
    public UserDto getUserById(Long userId) {
        var rez = userStorage.findById(userId).orElseThrow(() -> {
            throw new NotFoundParametrException(String.format("Отсутствует запрашиваемый пользователь. userId= %d",
                    userId));
        });
        return UserMapper.toUserDto(rez);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        userStorage.deleteById(userId);
    }

}
