package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.now().plusSeconds(1);
        LocalDateTime endDate = LocalDateTime.now().plusSeconds(2);
        String start = startDate.format(formatter);
        String end = endDate.format(formatter);

        UserDto bookerDto = new UserDto(
                1L,
                "Alexey",
                "alexey@ya.ru");

        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                itemDto,
                start,
                end,
                bookerDto,
                BookingStatus.WAITING.toString());

        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(itemDto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.item.userId").isEqualTo(itemDto.getUserId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.item.lastBooking.id").isEqualTo(itemDto.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.item.lastBooking.bookerId").isEqualTo(itemDto.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.item.nextBooking.id").isEqualTo(itemDto.getNextBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.item.nextBooking.bookerId").isEqualTo(itemDto.getNextBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.item.comments").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start);
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bookerDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(bookerDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(bookerDto.getEmail());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.WAITING.toString());
    }
}