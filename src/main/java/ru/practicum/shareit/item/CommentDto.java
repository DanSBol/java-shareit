package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "CommentDtoBuilder")
public class CommentDto implements Serializable {
    private Long id;
    private String authorName;
    private Long itemId;
    @NotBlank
    private String text;
    private String created;

    public static class CommentDtoBuilder {
        public CommentDtoBuilder() {
            // Пустой конструктор
        }
    }
}