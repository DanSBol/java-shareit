package ru.practicum.shareit.booking;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "BookingShotDtoBuilder")
public class BookingShotDto implements Serializable {
    private Long id;
    private Long bookerId;

    public static class BuilderDtoBuilder {
        public BuilderDtoBuilder() {
            // Пустой конструктор
        }
    }
}