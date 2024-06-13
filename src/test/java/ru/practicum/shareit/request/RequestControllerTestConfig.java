package ru.practicum.shareit.request;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class RequestControllerTestConfig {
    @Bean
    public RequestService requestService() {
        return mock(RequestService.class);
    }
}