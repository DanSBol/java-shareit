package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder(builderClassName = "UserBuilder")
public class User {
    long id;
    String name;
    @NotNull
    @Email(message = "User Email invalid.")
    String email;
    
    public static class UserBuilder
    {
        public UserBuilder()
        {
            // Пустой конструктор
        }
    }
}
