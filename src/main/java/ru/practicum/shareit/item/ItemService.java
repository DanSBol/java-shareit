package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    ItemDto addItem(long userId, Item item);

    ItemDto updateItem(long userId, long itemId, Item item);

    Collection<ItemDto> getItemsByOwner(long userId);

    Long getUserId(long itemId);

    ItemDto getItem(long itemId);

    Collection<ItemDto> search(String text);
}