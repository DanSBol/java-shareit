package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface BookingService {

    Booking addBooking(long userId, Booking booking);

    Collection<Booking> getBooking(long userId, long itemId, LocalDate startDate, LocalDate endDate);

    Optional<Booking> getBooking(long bookingId);

    Optional<Booking> setBookingStatus(long userId, long bookingId, BookingStatus bookingStatus);
}