package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder(builderClassName = "UserDtoBuilder")
public class UserDto {
    long id;
    String name;
    @NotNull
    @Email(message = "User Email invalid.")
    String email;

    public static class UserDtoBuilder {
        public UserDtoBuilder() {
            // Пустой конструктор
        }
    }
}