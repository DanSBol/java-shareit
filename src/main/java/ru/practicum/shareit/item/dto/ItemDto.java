package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@Builder(builderClassName = "ItemDtoBuilder")
public class ItemDto {
    long id;
    String name;
    String description;
    boolean available;
    ItemRequest request;

    public static class ItemDtoBuilder {
        public ItemDtoBuilder() {
            // Пустой конструктор
        }
    }
}