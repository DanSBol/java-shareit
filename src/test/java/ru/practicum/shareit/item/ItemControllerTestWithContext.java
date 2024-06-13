package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.config.WebConfig;
import ru.practicum.shareit.user.UserController;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig({ UserController.class, ItemControllerTestConfig.class, WebConfig.class})
class ItemControllerTestWithContext {
    private final ObjectMapper mapper = new ObjectMapper();

    private final ItemService itemService;

    private MockMvc mvc;

    private ItemDto itemDto;

    private HttpHeaders headers;

    @Autowired
    ItemControllerTestWithContext(ItemService itemService) {
        this.itemService = itemService;
    }

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
            .webAppContextSetup(wac)
            .build();

        itemDto = new ItemDto(
            1L,
            1L,
            "Microwave oven",
            "Power compact microwave oven",
            true,
            null,
            null,
            null,
            null);

        headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
    }

    @Test
    void addItem() throws Exception {
        ItemDto forQueryItemDto = new ItemDto();
        forQueryItemDto.setName("Microwave oven");
        forQueryItemDto.setDescription("Power compact microwave oven");
        forQueryItemDto.setAvailable(true);

        when(itemService.addItem(eq(1L), any()))
            .thenReturn(itemDto);

        mvc.perform(post("/items")
                .headers(headers)
                .content(mapper.writeValueAsString(forQueryItemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(itemDto.getName())))
            .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
            .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).addItem(eq(1L), any());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItem() throws Exception {
        ItemDto updateForItemDto = new ItemDto();
        updateForItemDto.setName("Oven");
        updateForItemDto.setDescription("Old oven");
        updateForItemDto.setAvailable(false);

        ItemDto newItemDto = new ItemDto(
            1L,
            1L,
            "Oven",
            "Old oven",
            false,
            null,
            null,
            null,
            null);

        when(itemService.updateItem(eq(1L), eq(itemDto.getId()), any()))
            .thenReturn(newItemDto);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                .headers(headers)
                .content(mapper.writeValueAsString(updateForItemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(newItemDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(newItemDto.getName())))
            .andExpect(jsonPath("$.description", is(newItemDto.getDescription())))
            .andExpect(jsonPath("$.available", is(newItemDto.getAvailable())));

        verify(itemService, times(1)).updateItem(eq(1L), eq(itemDto.getId()), any());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void deleteItem() throws Exception {
        doNothing().when(itemService).deleteItem(eq(1L), eq(itemDto.getId()));

        mvc.perform(delete("/items/{itemId}", itemDto.getId())
                .headers(headers))
            .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItem(eq(1L), eq(itemDto.getId()));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItem(eq(1L), eq(itemDto.getId())))
            .thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", itemDto.getId())
                .headers(headers))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(itemDto.getName())))
            .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
            .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).getItem(eq(1L), eq(itemDto.getId()));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItemsByOwner(eq(1L), eq(0), eq(1000)))
            .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                .headers(headers)
                .param("from", "0")
                .param("size", "1000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
            .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
            .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
            .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).getItemsByOwner(eq(1L), eq(0), eq(1000));
        verifyNoMoreInteractions(itemService);
    }
}