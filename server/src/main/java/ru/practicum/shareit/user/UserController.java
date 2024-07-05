package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                           @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}