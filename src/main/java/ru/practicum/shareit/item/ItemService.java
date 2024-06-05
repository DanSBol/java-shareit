package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ItemService {

    @Transactional
    ItemDto addNewItem(long userId, ItemDto itemDto);

    @Transactional
    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    @Transactional
    void deleteItem(long userId, long itemId);

    ItemDto getItem(long userId, long itemId);

    List<ItemDto> getItemsByOwner(long userId);

    List<ItemDto> search(String text);

    @Transactional
    CommentDto addNewComment(long userId, long itemId, CommentDto commentDto);
}