package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private long id;
    private long userId;
    @NotBlank
    private String description;
    private String created;
    private List<ItemDto> items;
}