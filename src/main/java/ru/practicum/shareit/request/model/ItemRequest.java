package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder(builderClassName = "ItemRequestBuilder")
public class ItemRequest {
    long id;
    String description;
    long requester;
    LocalDate created;

    public static class ItemRequestBuilder {
        public ItemRequestBuilder() {
            // Пустой конструктор
        }
    }
}