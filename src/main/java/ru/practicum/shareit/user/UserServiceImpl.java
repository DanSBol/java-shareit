package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

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
        } else {
            User user = UserMapper.getUserFromDto(++id, userDto);
            users.put(user.getId(), user);
            userEmails.put(user.getEmail(), user.getId());
            return UserMapper.getDtoFromUser(users.get(user.getId()));
        }
    }

    @Override
    public Optional<UserDto> updateUser(long userId, UserDto userDto) {
        if (users.containsKey(userId)) {
            if (users.get(userId).equals(UserMapper.getUserFromDto(userId, userDto))) {
                throw new ValidationException("User duplicate update.");
            }
            if (userEmails.containsKey(userDto.getEmail()) && userEmails.get(userDto.getEmail()) != userId) {
                throw new ValidationException("Email duplicate creation.");
            } else {
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
                return Optional.of(UserMapper.getDtoFromUser(user));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserDto> getUser(long userId) {
        if (users.containsKey(userId)) {
            return Optional.of(UserMapper.getDtoFromUser(users.get(userId)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserDto> deleteUser(long userId) {
        Optional<UserDto> optUserDto = Optional.empty();
        if (users.containsKey(userId)) {
            User user = users.get(userId);
            optUserDto = Optional.of(UserMapper.getDtoFromUser(user));
            userEmails.remove(user.getEmail());
            users.remove(userId);
        }
        return optUserDto;
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<UserDto> collectionUserDto = new ArrayList<>();
        for (User user : users.values()) {
            collectionUserDto.add(UserMapper.getDtoFromUser(user));
        }
        return collectionUserDto;
    }
}