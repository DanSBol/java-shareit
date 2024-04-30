package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder(builderClassName = "UserDtoBuilder")
public class UserDto {
    private long id;
    private String name;
    @NotNull
    @Email(message = "User Email invalid.")
    private String email;

    public static class UserDtoBuilder {
        public UserDtoBuilder() {
            // Пустой конструктор
        }
    }
}