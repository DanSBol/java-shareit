package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getBookingByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(required = false, defaultValue = "ALL") String state,
                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @RequestParam(required = false, defaultValue = "20") Integer size) {
        return bookingService.getBookingByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(required = false, defaultValue = "ALL") String state,
                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @RequestParam(required = false, defaultValue = "20") Integer size) {
        return bookingService.getBookingByOwner(userId, state, from, size);
    }
}