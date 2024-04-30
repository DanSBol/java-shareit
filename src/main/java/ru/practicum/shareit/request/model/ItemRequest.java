package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder(builderClassName = "ItemRequestBuilder")
public class ItemRequest {
    private long id;
    private String description;
    private long requester;
    private LocalDate created;

    public static class ItemRequestBuilder {
        public ItemRequestBuilder() {
            // Пустой конструктор
        }
    }
}