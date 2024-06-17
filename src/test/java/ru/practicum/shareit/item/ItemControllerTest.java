package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingShotDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.error.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemDto itemDto;
    private BookingShotDto lastBooking;
    private BookingShotDto nextBooking;

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(ErrorHandler.class)
            .build();

        lastBooking = new BookingShotDto(1L, 1L);
        nextBooking = new BookingShotDto(3L, 1L);

        itemDto = new ItemDto(
            1L,
            1L,
            "Microwave oven",
            "Power compact microwave oven",
            true,
            lastBooking,
            nextBooking,
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
            .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
            .andExpect(jsonPath("$.lastBooking.id", is(itemDto.getLastBooking().getId()), Long.class))
            .andExpect(jsonPath("$.lastBooking.bookerId",
                    is(itemDto.getLastBooking().getBookerId()), Long.class))
            .andExpect(jsonPath("$.nextBooking.id", is(itemDto.getNextBooking().getId()), Long.class))
            .andExpect(jsonPath("$.nextBooking.bookerId",
                    is(itemDto.getNextBooking().getBookerId()), Long.class));

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
            lastBooking,
            nextBooking,
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
            .andExpect(jsonPath("$.available", is(newItemDto.getAvailable())))
            .andExpect(jsonPath("$.lastBooking.id", is(itemDto.getLastBooking().getId()), Long.class))
            .andExpect(jsonPath("$.lastBooking.bookerId",
                    is(itemDto.getLastBooking().getBookerId()), Long.class))
            .andExpect(jsonPath("$.nextBooking.id", is(itemDto.getNextBooking().getId()), Long.class))
            .andExpect(jsonPath("$.nextBooking.bookerId",
                    is(itemDto.getNextBooking().getBookerId()), Long.class));

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
            .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
            .andExpect(jsonPath("$.lastBooking.id", is(itemDto.getLastBooking().getId()), Long.class))
            .andExpect(jsonPath("$.lastBooking.bookerId",
                    is(itemDto.getLastBooking().getBookerId()), Long.class))
            .andExpect(jsonPath("$.nextBooking.id", is(itemDto.getNextBooking().getId()), Long.class))
            .andExpect(jsonPath("$.nextBooking.bookerId",
                    is(itemDto.getNextBooking().getBookerId()), Long.class));

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
            .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
            .andExpect(jsonPath("$[0].lastBooking.id", is(itemDto.getLastBooking().getId()), Long.class))
            .andExpect(jsonPath("$[0].lastBooking.bookerId",
                    is(itemDto.getLastBooking().getBookerId()), Long.class))
            .andExpect(jsonPath("$[0].nextBooking.id", is(itemDto.getNextBooking().getId()), Long.class))
            .andExpect(jsonPath("$[0].nextBooking.bookerId",
                    is(itemDto.getNextBooking().getBookerId()), Long.class));

        verify(itemService, times(1)).getItemsByOwner(eq(1L), eq(0), eq(1000));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void search() throws Exception {
        when(itemService.search(eq("oven"), eq(0), eq(1000)))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .headers(headers)
                        .param("text", "oven")
                        .param("from", "0")
                        .param("size", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemDto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.bookerId",
                        is(itemDto.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemDto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.bookerId",
                        is(itemDto.getNextBooking().getBookerId()), Long.class));

        verify(itemService, times(1)).search(eq("oven"), eq(0), eq(1000));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void addNewComment() throws Exception {
        CommentDto putCommentDto = new CommentDto();
        putCommentDto.setItemId(itemDto.getId());
        putCommentDto.setAuthorName("Alexey");
        putCommentDto.setText("Cool");

        CommentDto getCommentDto = putCommentDto;
        getCommentDto.setId(1L);

        when(itemService.addComment(eq(1L), eq(itemDto.getId()), any()))
                .thenReturn(getCommentDto);

        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .headers(headers)
                        .content(mapper.writeValueAsString(putCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(getCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(getCommentDto.getAuthorName())))
                .andExpect(jsonPath("$.itemId", is(getCommentDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.text", is(getCommentDto.getText())));

        verify(itemService, times(1)).addComment(eq(1L), eq(itemDto.getId()), any());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void addNewComment_400_bad_request() throws Exception {
        CommentDto putCommentDto = new CommentDto();
        putCommentDto.setItemId(itemDto.getId());
        putCommentDto.setAuthorName("Alexey");
        putCommentDto.setText("");

        when(itemService.addComment(eq(1L), eq(itemDto.getId()), any()))
                .thenThrow(BadRequestException.class);

        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .headers(headers)
                        .content(mapper.writeValueAsString(putCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, times(1)).addComment(eq(1L), eq(itemDto.getId()), any());
        verifyNoMoreInteractions(itemService);
    }
}