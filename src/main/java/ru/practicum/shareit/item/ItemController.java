package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.IllegalArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody Map<String, Object> dataMap) {
        if (userService.getUser(userId).isPresent()) {
            if (dataMap.containsKey("name") & dataMap.containsKey("description") & dataMap.containsKey("available")) {
                return itemService.addItem(userId, (String) dataMap.get("name"),
                        (String) dataMap.get("description"),
                        (Boolean) dataMap.get("available"));
            } else {
                throw new IllegalArgumentException("Not expected request body");
            }
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable(value = "itemId") Integer itemId,
                              @RequestBody Map<String, Object> dataMap) {
        if (userService.getUser(userId).isPresent()) {
            if (dataMap.containsKey("available") & dataMap.size() == 1) {
                return itemService.updateItemAvailable(userId, itemId, (Boolean) dataMap.get("available")).orElseThrow(() ->
                        new NotFoundException("Item not found."));
            } else if (!dataMap.containsKey("available")) {
                return itemService.updateItemNameDesc(userId, itemId, (String) dataMap.getOrDefault("name", null),
                        (String) dataMap.getOrDefault("description", null)).orElseThrow(() ->
                        new NotFoundException("Item not found."));
            } else if (dataMap.containsKey("available")) {
                return itemService.updateItem(userId, itemId, (String) dataMap.getOrDefault("name", null),
                        (String) dataMap.getOrDefault("description", null),
                        (Boolean) dataMap.get("available")).orElseThrow(() ->
                        new NotFoundException("Item not found."));
            }
        } else {
            throw new NotFoundException("User not found");
        }
        throw new IllegalArgumentException("Not expected request body");
    }

    @GetMapping
    public Collection<ItemDto> getItem(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable(value = "itemId") Integer itemId) {
        return itemService.getItem(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam(value = "text") String text) {
        return itemService.search(text);
    }
}