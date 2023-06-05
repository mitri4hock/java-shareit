package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictParametrException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserStorage userStorage;



    public UserDto createUser(User user) {
//        if (isUsersEmailDuplicate(user).size() != 0) {
//            throw new ConflictParametrException("при создании пользователя передан Email уже существующего пользователя");
//        }
        return UserMapper.toUserDto(userStorage.save(user));
    }

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

    private List<User> isUsersEmailDuplicate(User user) {
        return userStorage.findByEmailContainingIgnoreCase(user.getEmail());
    }
}
