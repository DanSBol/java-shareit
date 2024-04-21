package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    UserDto getUser(long userId);

    UserDto deleteUser(long userId);

    Collection<UserDto> getAllUsers();
}