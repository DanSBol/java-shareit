package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoJsonTest {

    @Autowired
    private JacksonTester<RequestDto> json;

    @Test
    void testRequestDto() throws Exception {
        String created = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        RequestDto requestDto = new RequestDto(
                1L,
                1L,
                "TV",
                created,
                null);

        JsonContent<RequestDto> result = json.write(requestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("TV");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created);
        assertThat(result).extractingJsonPathStringValue("$.items").isEqualTo(null);
    }
}