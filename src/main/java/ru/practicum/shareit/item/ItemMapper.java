package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Item mapToItem(ItemDto itemDto, User user, Request request) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setRequest(request);
        return item;
    }

    public static ItemDto mapToItemDto(Item item, BookingShotDto lastBooking, BookingShotDto nextBooking, Set<CommentDto> commentsDto) {
        return new ItemDto.ItemDtoBuilder()
            .id(item.getId())
            .userId(item.getOwner().getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.getAvailable())
            .lastBooking(lastBooking)
            .nextBooking(nextBooking)
            .comments(commentsDto != null ? commentsDto : new HashSet<>())
            .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
            .build();
    }
}