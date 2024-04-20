package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(builderClassName = "BookingBuilder")
public class Booking {
    long id;
    long item;
    LocalDate start;
    LocalDate end;
    long booker;
    BookingStatus status;
    public static class BookingBuilder
    {
        public BookingBuilder(){}
    }
}