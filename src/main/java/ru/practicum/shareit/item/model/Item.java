package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotEmpty;

@Data
@Builder(builderClassName = "ItemBuilder")
public class Item {
    long id;
    @NotEmpty
    String name;
    @NotEmpty
    String description;
    boolean available;
    long owner;
    ItemRequest request;

    public static class ItemBuilder {
        public ItemBuilder() {
            // Пустой конструктор
        }
    }
}