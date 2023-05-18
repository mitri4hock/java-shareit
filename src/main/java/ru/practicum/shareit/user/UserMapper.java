package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail() != null ? user.getEmail() : null)
                .build();
    }

    public static User toUser(UserDto userDto, Long userId) {
        return User.builder()
                .id(userId)
                .name(userDto.getName() != null ? userDto.getName() : null)
                .email(userDto.getEmail() != null ? userDto.getEmail() : null)
                .build();
    }
}
