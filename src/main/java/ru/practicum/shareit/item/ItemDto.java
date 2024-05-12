package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import ru.practicum.shareit.booking.BookingShotDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "ItemDtoBuilder")
public class ItemDto implements Serializable {
    private Long id;
    private Long userId;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private BookingShotDto lastBooking;
    private BookingShotDto nextBooking;
    private Long requestId;
    private Set<CommentDto> comments;

    public static class ItemDtoBuilder {
        public ItemDtoBuilder() {
            // Пустой конструктор
        }
    }
}