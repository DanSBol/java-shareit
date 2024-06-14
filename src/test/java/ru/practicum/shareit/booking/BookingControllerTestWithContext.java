package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.config.WebConfig;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig({ BookingController.class, BookingControllerTestConfig.class, WebConfig.class})
class BookingControllerTestWithContext {
    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDto userDto;
    private ItemDto itemDto;
    private BookingDto bookingDto;

    private HttpHeaders headers;

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();

        userDto = new UserDto(
                1L,
                "John",
                "john.doe@mail.com");

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

        bookingDto = new BookingDto(
                1L,
                1L,
                itemDto,
                "2024.06.14T00:00:00",
                "2024.06.15T00:00:00",
                userDto,
                null);

        headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
    }

    @Test
    void addBooking() throws Exception {
        ItemDto forQueryItemDto = new ItemDto();
        forQueryItemDto.setName("Microwave oven");
        forQueryItemDto.setDescription("Power compact microwave oven");
        forQueryItemDto.setAvailable(true);

        when(bookingService.addBooking(eq(1L), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .headers(headers)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));

        verify(bookingService, times(1)).addBooking(eq(1L), any());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void approveBooking() throws Exception {
        BookingDto approvedBookingDto = bookingDto;
        approvedBookingDto.setStatus("APPROVED");
        BookingDto rejectedBookingDto = bookingDto;
        rejectedBookingDto.setStatus("REJECTED");

        when(bookingService.approveBooking(eq(userDto.getId()), eq(bookingDto.getId()), eq(true)))
                .thenReturn(approvedBookingDto);
        when(bookingService.approveBooking(eq(userDto.getId()), eq(bookingDto.getId()), eq(false)))
                .thenReturn(rejectedBookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .headers(headers)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(approvedBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(approvedBookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.item", is(approvedBookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.start", is(approvedBookingDto.getStart())))
                .andExpect(jsonPath("$.end", is(approvedBookingDto.getEnd())))
                .andExpect(jsonPath("$.booker", is(approvedBookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.status", is(approvedBookingDto.getStatus())));

        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .headers(headers)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(rejectedBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(rejectedBookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.item", is(rejectedBookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.start", is(rejectedBookingDto.getStart())))
                .andExpect(jsonPath("$.end", is(rejectedBookingDto.getEnd())))
                .andExpect(jsonPath("$.booker", is(rejectedBookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.status", is(rejectedBookingDto.getStatus())));

        verify(bookingService, times(2)).approveBooking(eq(userDto.getId()),
                eq(bookingDto.getId()), anyBoolean());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(eq(userDto.getId()), eq(bookingDto.getId())))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));

        verify(bookingService, times(1)).getBooking(eq(userDto.getId()), eq(itemDto.getId()));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingByBooker() throws Exception {
        when(bookingService.getBookingByBooker(eq(userDto.getId()), eq("WAITING"), eq(0), eq(1000)))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .headers(headers)
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("size", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus())));

        verify(bookingService, times(1)).getBookingByBooker(eq(userDto.getId()),
                eq("WAITING"), eq(0), eq(1000));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingByOwner() throws Exception {
        when(bookingService.getBookingByOwner(eq(userDto.getId()), eq("WAITING"), eq(0), eq(1000)))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .headers(headers)
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("size", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus())));

        verify(bookingService, times(1)).getBookingByOwner(eq(userDto.getId()),
                eq("WAITING"), eq(0), eq(1000));
        verifyNoMoreInteractions(bookingService);
    }
}