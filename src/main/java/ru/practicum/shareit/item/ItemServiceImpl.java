package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.IllegalArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final Map<Long, Item> items = new HashMap<>();

    private int id = 0;

    @Override
    public ItemDto addItem(long userId, Item item) {
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
    public Optional<ItemDto> updateItem(long userId, long itemId, Item item) {
        if (items.containsKey(itemId)) {
            Item itemFromItems = items.get(itemId);
            if (itemFromItems.getOwner() == userId) {
                if (item.getName() != null) {
                    itemFromItems.setName(item.getName());
                }
                if (item.getDescription() != null) {
                    itemFromItems.setDescription(item.getDescription());
                }
                if ((item.getName() == null & item.getDescription() == null) | item.getId() != 0) {
                    itemFromItems.setAvailable(item.isAvailable());
                }
                items.put(itemId, itemFromItems);
                return Optional.of(ItemMapper.toItemDto(itemFromItems));
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