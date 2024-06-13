package ru.practicum.shareit.request;

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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig({ RequestController.class, RequestControllerTestConfig.class, WebConfig.class})
class RequestControllerTestWithContext {
    private final ObjectMapper mapper = new ObjectMapper();

    private final RequestService requestService;

    private MockMvc mvc;

    private RequestDto requestDto;

    private HttpHeaders headers;

    @Autowired
    RequestControllerTestWithContext(RequestService requestService) {
        this.requestService = requestService;
    }

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
            .webAppContextSetup(wac)
            .build();

        requestDto = new RequestDto(
                1L,
                1L,
                "microwave oven",
                null,
                new ArrayList<>());

        headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
    }

    @Test
    void addRequest() throws Exception {
        RequestDto setRequestDto = new RequestDto();
        setRequestDto.setDescription(requestDto.getDescription());

        when(requestService.addRequest(eq(1L), any()))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .headers(headers)
                        .content(mapper.writeValueAsString(setRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.userId", is(requestDto.getUserId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));

        verify(requestService, times(1)).addRequest(eq(1L), any());
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void getRequest() throws Exception {
        when(requestService.getRequest(eq(1L), eq(requestDto.getId())))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/{requestId}", requestDto.getId())
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.userId", is(requestDto.getUserId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));

        verify(requestService, times(1)).getRequest(eq(1L), eq(requestDto.getId()));
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void getRequestsByOwner() throws Exception {
        when(requestService.getRequestsByOwner(eq(1L)))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].userId", is(requestDto.getUserId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())));

        verify(requestService, times(1)).getRequestsByOwner(eq(1L));
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void getRequestsByParam() throws Exception {
        when(requestService.getRequestsByParam(eq(1L), eq(0), eq(1000)))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all")
                        .headers(headers)
                        .param("from", "0")
                        .param("size", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].userId", is(requestDto.getUserId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())));

        verify(requestService, times(1)).getRequestsByParam(eq(1L), eq(0),
                eq(1000));
        verifyNoMoreInteractions(requestService);
    }
}