package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(long userId, BookingDto bookingDto);

    BookingDto approveBooking(long userId, long bookingId, boolean approved);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getBooking(long userId, String state, int from, int size);

    List<BookingDto> getBookingByOwner(long userId, String state, int from, int size);
}