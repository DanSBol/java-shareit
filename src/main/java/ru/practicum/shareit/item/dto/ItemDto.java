package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@Builder(builderClassName = "ItemDtoBuilder")
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private ItemRequest request;

    public static class ItemDtoBuilder {
        public ItemDtoBuilder() {
            // Пустой конструктор
        }
    }
}