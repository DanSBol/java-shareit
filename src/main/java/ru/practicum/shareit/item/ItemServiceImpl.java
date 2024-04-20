package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.IllegalArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.*;

import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final Map<Long, Item> items = new HashMap<>();

    private int id = 0;

    @Override
    public ItemDto addItem(long userId, String name, String description, boolean available) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty item name.");
        }
        if (description.isEmpty()) {
            throw new IllegalArgumentException("empty item description.");
        }
        Item item = new Item.ItemBuilder()
                .id(++id)
                .name(name)
                .description(description)
                .available(available)
                .owner(userId)
                .build();
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(items.get(item.getId()));
    }

    @Override
    public Optional<ItemDto> updateItemNameDesc(long userId, long itemId, String name, String description) {
        if (items.containsKey(itemId)) {
            if (items.get(itemId).getOwner() == userId) {
                Item item = items.get(itemId);
                if (name != null) {
                    item.setName(name);
                }
                if (description != null) {
                    item.setDescription(description);
                }
                items.put(itemId, item);
                return Optional.of(ItemMapper.toItemDto(item));
            } else {
                throw new NotFoundException("This item has another owner.");
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemDto> updateItem(long userId, long itemId, String name, String description, boolean availability) {
        if (items.containsKey(itemId)) {
            if (items.get(itemId).getOwner() == userId) {
                Item item = items.get(itemId);
                if (name != null) {
                    item.setName(name);
                }
                if (description != null) {
                    item.setDescription(description);
                }
                item.setAvailable(availability);
                items.put(itemId, item);
                return Optional.of(ItemMapper.toItemDto(item));
            } else {
                throw new NotFoundException("This item has another owner.");
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemDto> updateItemAvailable(long userId, long itemId, boolean available) {
        if (items.containsKey(itemId)) {
            if (items.get(itemId).getOwner() == userId) {
                Item item = items.get(itemId);
                item.setAvailable(available);
                items.put(itemId, item);
                return Optional.of(ItemMapper.toItemDto(item));
            } else {
                throw new NotFoundException("This item has another owner.");
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<ItemDto> getItemsByOwner(long userId) {
        Collection<ItemDto> itemDtoCollection = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == userId) {
                itemDtoCollection.add(ItemMapper.toItemDto(item));
            }
        }
        return itemDtoCollection;
    }

    @Override
    public Optional<Long> getUserId(long itemId) {
        if (items.containsKey(itemId)) {
            return Optional.of(items.get(itemId).getOwner());
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemDto> getItem(long itemId) {
        if (items.containsKey(itemId)) {
            return Optional.of(ItemMapper.toItemDto(items.get(itemId)));
        }
        return Optional.empty();
    }

    @Override
    public Collection<ItemDto> search(String text) {
        Collection<ItemDto> itemDtoCollection = new ArrayList<>();
        if (!text.isEmpty()) {
            for (Item item : items.values()) {
                if ((item.getName().toLowerCase().contains(text.toLowerCase()) |
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) & item.isAvailable()) {
                    itemDtoCollection.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return itemDtoCollection;
    }
}