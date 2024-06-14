package ru.practicum.shareit.booking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.user.UserService;

import static org.mockito.Mockito.mock;

@Configuration
public class BookingControllerTestConfig {
    @Bean
    public BookingService bookingService() {
        return mock(BookingService.class);
    }
}