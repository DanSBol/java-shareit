package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.config.WebConfig;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig({UserController.class, UserControllerTestConfig.class, WebConfig.class})
class UserControllerTestWithContext {
    private final ObjectMapper mapper = new ObjectMapper();

    private final UserService userService;

    private MockMvc mvc;

    private UserDto userDto;

    @Autowired
    UserControllerTestWithContext(UserService userService) {
        this.userService = userService;
    }

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
            .webAppContextSetup(wac)
            .build();

        userDto = new UserDto(
                1L,
                "John",
                "john.doe@mail.com");
    }

    @Test
    void addUser() throws Exception {
        when(userService.addUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).addUser(any());
        verifyNoMoreInteractions(userService);
    }

    @Test
    void updateUser() throws Exception {

        UserDto updateForUserDto = new UserDto();
        updateForUserDto.setName("Alexey");
        updateForUserDto.setEmail("alexey.doe@mail.com");

        UserDto newUserDto = new UserDto(
                1L,
                "Alexey",
                "alexey.doe@mail.com");

        when(userService.updateUser(eq(userDto.getId()), any()))
                .thenReturn(newUserDto);

        mvc.perform(patch("/users/{userId}", userDto.getId())
                        .content(mapper.writeValueAsString(updateForUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(newUserDto.getName())))
                .andExpect(jsonPath("$.email", is(newUserDto.getEmail())));

        verify(userService, times(1)).updateUser(eq(userDto.getId()), any());
        verifyNoMoreInteractions(userService);
    }

    @Test
    void deleteUser() throws Exception {

        doNothing().when(userService).deleteUser(userDto.getId());

        mvc.perform(delete("/users/{userId}", userDto.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userDto.getId());
        //verifyNoMoreInteractions(userService);
    }

    @Test
    void getUser() throws Exception {
        when(userService.getUser(userDto.getId()))
                .thenReturn(userDto);

        mvc.perform(get("/users/{userId}", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).getUser(userDto.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));

        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }
}