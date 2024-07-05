package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private long id;
    private String authorName;
    private long itemId;
    @NotBlank
    private String text;
    private String created;
}