package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserService;
import java.util.Collection;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody Item item) {
        if (userService.getUser(userId).isPresent()) {
            return itemService.addItem(userId, item);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable(value = "itemId") Integer itemId,
                              @RequestBody Item item) {
        if (userService.getUser(userId).isPresent()) {
            return itemService.updateItem(userId, itemId, item).orElseThrow(() ->
                    new NotFoundException("Item not found."));
        } else {
            throw new NotFoundException("User not found");
        }
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