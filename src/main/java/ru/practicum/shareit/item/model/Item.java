package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(builderClassName = "ItemBuilder")
public class Item {
    long id;
    String name;
    String description;
    boolean available;
    long owner;
    ItemRequest request;
    
    public static class ItemBuilder
    {
        public ItemBuilder() 
        {
            // Пустой конструктор
        }
    }
}
