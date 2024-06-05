package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "BookingDtoBuilder")
public class BookingDto {
    private Long id;
    private Long itemId;
    private ItemDto item;
    private String start;
    private String end;
    private UserDto booker;
    private String status;

    public static class BuilderDtoBuilder {
        public BuilderDtoBuilder() {
            // Пустой конструктор
        }
    }
}
