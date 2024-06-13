package ru.practicum.shareit.item;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.user.UserService;

import static org.mockito.Mockito.mock;

@Configuration
public class ItemControllerTestConfig {
    @Bean
    public ItemService itemService() {
        return mock(ItemService.class);
    }
}