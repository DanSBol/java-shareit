package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.IllegalArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final Map<Long, Item> items = new HashMap<>();
    private int id = 0;
    private final UserService userService;

    @Override
    public ItemDto addItem(long userId, Item item) {
        if (userService.getUser(userId) == null) {
            throw new NotFoundException("User not found");
        }
        if (item.getName().isEmpty()) {
            throw new IllegalArgumentException("empty item name.");
        }
        if (item.getDescription().isEmpty()) {
            throw new IllegalArgumentException("empty item description.");
        }
        if (!item.isAvailable()) {
            throw new IllegalArgumentException("unavailable item create.");
        }
        item.setId(++id);
        item.setOwner(userId);
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item not found.");
        }
        Item itemFromItems = items.get(itemId);
        if (itemFromItems.getOwner() != userId) {
            throw new NotFoundException("This item has another owner.");
        }
        if (item.getName() != null) {
            itemFromItems.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemFromItems.setDescription(item.getDescription());
        }
        if ((item.getName() == null && item.getDescription() == null) || item.getId() != 0) {
            itemFromItems.setAvailable(item.isAvailable());
        }
        items.put(itemId, itemFromItems);
        return ItemMapper.toItemDto(itemFromItems);
    }

    @Override
    public Collection<ItemDto> getItemsByOwner(long userId) {
        return items.values().stream()
            .filter(item -> userId == item.getOwner())
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
    }

    @Override
    public Long getUserId(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item not found.");
        }
        return items.get(itemId).getOwner();
    }

    @Override
    public ItemDto getItem(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item not found.");
        }
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
            .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.isAvailable())
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
    }
}