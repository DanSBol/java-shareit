package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingShotDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
        BookingShotDto nextBooking = new BookingShotDto(3L, 1L);
        BookingShotDto lastBooking = new BookingShotDto(1L, 1L);
        ItemDto itemDto = new ItemDto(
                1L,
                1L,
                "TV",
                "Large color TV",
                true,
                lastBooking,
                nextBooking,
                null,
                null);

        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("TV");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Large color TV");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(lastBooking.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(lastBooking.getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(nextBooking.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(nextBooking.getBookerId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.requestId").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.comments").isEqualTo(null);
    }
}