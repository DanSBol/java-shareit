package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
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
    public UserDto updateUser(@PathVariable(value = "userId") Long userId,
                           @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto).orElseThrow(() -> new NotFoundException("User not found."));
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable(value = "userId") Long userId) {
        return userService.getUser(userId).orElseThrow(() -> new NotFoundException("User not found."));
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUser(@PathVariable(value = "userId") Long userId) {
        return userService.deleteUser(userId).orElseThrow(() -> new NotFoundException("User not found."));
    }
}