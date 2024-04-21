package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody Item item) {
        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable(value = "itemId") Integer itemId,
                              @RequestBody Item item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping
    public Collection<ItemDto> getItem(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable(value = "itemId") Integer itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam(value = "text") String text) {
        return itemService.search(text);
    }
}