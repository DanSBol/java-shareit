package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.Optional;

public interface ItemService {

    ItemDto addItem(long userId, String name, String description, boolean available);

    Optional<ItemDto> updateItemNameDesc(long userId, long itemId, String name, String description);

    Optional<ItemDto> updateItemAvailable(long userId, long itemId, boolean available);

    Optional<ItemDto> updateItem(long userId, long itemId, String name, String description, boolean availability);

    Collection<ItemDto> getItemsByOwner(long userId);

    Optional<Long> getUserId(long itemId);

    Optional<ItemDto> getItem(long itemId);

    Collection<ItemDto> search(String text);
}