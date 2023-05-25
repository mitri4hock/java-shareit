package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    private static final String valueIfNotProvided = "not provided";

    public static UserDto toUserDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail() != null ? user.getEmail() : valueIfNotProvided)
                .build();
    }

    public static User toUser(UserDto userDto, Long userId) {
        return User.builder()
                .id(userId)
                .name(userDto.getName() != null ? userDto.getName() : valueIfNotProvided)
                .email(userDto.getEmail())
                .build();
    }
}
