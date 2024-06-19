package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.error.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(ErrorHandler.class)
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
    void addUser_400_bad_request() throws Exception {
        when(userService.addUser(any())).thenThrow(new BadRequestException("User Email invalid."));
        userDto.setEmail("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

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
    void updateUser_404_not_found() throws Exception {

        UserDto updateForUserDto = new UserDto();
        updateForUserDto.setName("Alexey");
        updateForUserDto.setEmail("alexey.doe@mail.com");

        UserDto newUserDto = new UserDto(
                1L,
                "Alexey",
                "alexey.doe@mail.com");

        when(userService.updateUser(eq(999L), any()))
                .thenThrow(new NotFoundException("User not found."));

        mvc.perform(patch("/users/{userId}", 999L)
                        .content(mapper.writeValueAsString(updateForUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(999L), any());
        verifyNoMoreInteractions(userService);
    }

    @Test
    void deleteUser() throws Exception {

        doNothing().when(userService).deleteUser(userDto.getId());

        mvc.perform(delete("/users/{userId}", userDto.getId()))
            .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userDto.getId());
        verifyNoMoreInteractions(userService);
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
    void getUser_404_not_found() throws Exception {

        when(userService.getUser(1)).thenThrow(new NotFoundException("User not found."));

        mvc.perform(get("/users/{userId}", 1))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUser(1);
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