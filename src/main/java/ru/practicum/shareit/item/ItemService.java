package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemService {

    ItemDto addItem(long userId, Item item);

    Optional<ItemDto> updateItem(long userId, long itemId, Item item);

    Collection<ItemDto> getItemsByOwner(long userId);

    Optional<Long> getUserId(long itemId);

    Optional<ItemDto> getItem(long itemId);

    Collection<ItemDto> search(String text);
}