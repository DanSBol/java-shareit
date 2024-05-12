package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUser(long userId);

    UserDto getUser(long userId);

    List<UserDto> getAllUsers();
}