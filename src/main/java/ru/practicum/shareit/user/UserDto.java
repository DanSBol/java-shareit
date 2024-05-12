package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder(builderClassName = "UserDtoBuilder")
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
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