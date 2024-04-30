package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder(builderClassName = "BookingBuilder")
public class Booking {
    private long id;
    private long item;
    private LocalDate start;
    private LocalDate end;
    private long booker;
    private BookingStatus status;

    public static class BookingBuilder {
        public BookingBuilder() {
            // Пустой конструктор
        }
    }
}