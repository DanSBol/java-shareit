package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final Map<Long, Booking> bookings = new HashMap<>();

    private long id = 0;

    @Override
    public Booking addBooking(long userId, Booking booking) {
        booking.setId(++id);
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Collection<Booking> getBooking(long userId, long itemId, LocalDate startDate, LocalDate endDate) {
        Collection<Booking> bookingCollection = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getItem() == itemId & !booking.getStart().isAfter(startDate) &
                    !booking.getEnd().isBefore(endDate)) {
                bookingCollection.add(booking);
            }
        }
        return bookingCollection;
    }

    @Override
    public Optional<Booking> getBooking(long bookingId) {
        if (bookings.containsKey(bookingId)) {
            return Optional.of(bookings.get(bookingId));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Booking> setBookingStatus(long userId, long bookingId, BookingStatus bookingStatus) {
        if (bookings.containsKey(bookingId)) {
            Booking booking = bookings.get(bookingId);
            booking.setStatus(bookingStatus);
            bookings.remove(bookingId);
            bookings.put(bookingId, booking);
            return Optional.of(booking);
        }
        return Optional.empty();
    }
}