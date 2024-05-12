package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToNewUser(userDto)));
    }

    @Transactional
    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow();
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        }
    }

    @Override
    public UserDto getUser(long userId) {
        return UserMapper.mapToUserDto(userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found.")));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.mapToUserDto(users);
    }
}