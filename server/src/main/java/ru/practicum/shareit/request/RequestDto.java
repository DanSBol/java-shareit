package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.ItemDto;

import java.util.List;

@Data
@Builder(builderClassName = "RequestDtoBuilder")
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private Long id;
    private Long userId;
    private String description;
    private String created;
    private List<ItemDto> items;

    public static class RequestDtoBuilder {
        public RequestDtoBuilder() {
            // Пустой конструктор
        }
    }
}