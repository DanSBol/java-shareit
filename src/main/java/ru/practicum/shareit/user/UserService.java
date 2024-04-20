package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    UserDto addUser(UserDto userDto);

    Optional<UserDto> updateUser(long userId, UserDto userDto);

    Optional<UserDto> getUser(long userId);

    Optional<UserDto> deleteUser(long userId);

    Collection<UserDto> getAllUsers();
}