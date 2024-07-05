package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShotDto;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private long id;
    private long userId;
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
}