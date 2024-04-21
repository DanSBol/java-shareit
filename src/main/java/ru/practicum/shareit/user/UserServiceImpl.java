package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> userEmails = new HashMap<>();
    private int id = 0;

    @Override
    public UserDto addUser(UserDto userDto) {
        if (userEmails.containsKey(userDto.getEmail())) {
            throw new ValidationException("Email duplicate creation.");
        }
        User user = UserMapper.toUser(++id, userDto);
        users.put(user.getId(), user);
        userEmails.put(user.getEmail(), user.getId());
        return UserMapper.toUserDto(users.get(user.getId()));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User not found.");
        }
        if (users.get(userId).equals(UserMapper.toUser(userId, userDto))) {
            throw new ValidationException("User duplicate update.");
        }
        if (userEmails.containsKey(userDto.getEmail()) && userEmails.get(userDto.getEmail()) != userId) {
            throw new ValidationException("Email duplicate creation.");
        }
        User user = users.get(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userEmails.remove(user.getEmail());
            user.setEmail(userDto.getEmail());
            userEmails.put(user.getEmail(), user.getId());
        }
        users.put(userId, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User not found.");
        }
        return UserMapper.toUserDto(users.get(userId));
    }

    @Override
    public UserDto deleteUser(long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User not found.");
        }
        User user = users.get(userId);
        UserDto userDto = UserMapper.toUserDto(user);
        userEmails.remove(user.getEmail());
        users.remove(userId);
        return userDto;
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return users.values().stream()
            .map(UserMapper::toUserDto)
            .collect(Collectors.toList());
    }
}