package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto.UserDtoBuilder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }

    public static User toUser(long userId, UserDto userDto) {
        return new User.UserBuilder()
            .id(userId)
            .name(userDto.getName())
            .email(userDto.getEmail())
            .build();
    }
}