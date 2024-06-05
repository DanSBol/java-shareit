package ru.practicum.shareit.user;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto.UserDtoBuilder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }

    public static User mapToNewUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static List<UserDto> mapToUserDto(List<User> users) {
        return users.stream()
            .map(UserMapper::mapToUserDto)
            .collect(Collectors.toList());
    }
}