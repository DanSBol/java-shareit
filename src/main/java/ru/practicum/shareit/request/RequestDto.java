package ru.practicum.shareit.request;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "RequestDtoBuilder")
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private Long id;
    private Long userId;
    private String description;
    private LocalDateTime created;

    public static class RequestDtoBuilder {
        public RequestDtoBuilder() {
            // Пустой конструктор
        }
    }
}

