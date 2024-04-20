package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final ItemService itemService;

    @PostMapping()
    public Booking addBooking(@RequestHeader("X-ShareIt-User-Id") Long userId,
                                  @RequestBody Booking booking) {
        return bookingService.addBooking(userId, booking);
    }

    @GetMapping()
    public Collection<Booking> getBooking(@RequestHeader("X-ShareIt-User-Id") Long userId,
                                          @RequestParam(value = "itemId") Integer itemId,
                                          @RequestParam(value = "startDate") LocalDate startDate,
                                          @RequestParam(value = "endDate") LocalDate endDate) {
        return bookingService.getBooking(userId, itemId, startDate, endDate);
    }

    @PatchMapping("/{bookingId}")
    public Booking approveBooking(@RequestHeader("X-ShareIt-User-Id") Long userId,
                                  @PathVariable(value = "bookingId") Long bookingId,
                                  @RequestParam(value = "status") BookingStatus bookingStatus) {
        Optional<Booking> optionalBooking = bookingService.getBooking(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            Optional<Long> optionalUserId = itemService.getUserId(booking.getItem());
            if (optionalUserId.isPresent()) {
                long ownerId = optionalUserId.get();
                if (ownerId == userId) {
                    return bookingService.setBookingStatus(userId, bookingId, bookingStatus).orElseThrow(() ->
                            new NotFoundException("Booking not found"));
                } else {
                    throw new NotFoundException("This item has another owner.");
                }
            } else {
                throw new NotFoundException("Item not found");
            }
        } else {
            throw new NotFoundException("Booking not found");
        }
    }
}