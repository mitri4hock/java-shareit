package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadParametrException;
import ru.practicum.shareit.exceptions.NotFoundParametrException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@RequestBody @NotNull User user, @PathVariable @NotNull Long userId) {
        if (userService.getUserById(userId).isEmpty()) {
            log.info("Попытка запросить редактирование отсутствующего пользователя. userId= {}", userId);
            throw new BadParametrException("Отсутствует запрашиваемый пользователь. userId= " + userId);
        }

        return userService.patchUser(user, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        var rez = userService.getUserById(userId);
        if (rez.isEmpty()) {
            throw new NotFoundParametrException("Отсутствует запрашиваемый пользователь. userId= " + userId);
        }
        return UserMapper.toUserDto(rez.get());
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}
