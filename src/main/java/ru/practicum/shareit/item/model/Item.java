package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotEmpty;

@Data
@Builder(builderClassName = "ItemBuilder")
public class Item {
    private long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    private boolean available;
    private long owner;
    private ItemRequest request;

    public static class ItemBuilder {
        public ItemBuilder() {
            // Пустой конструктор
        }
    }
}