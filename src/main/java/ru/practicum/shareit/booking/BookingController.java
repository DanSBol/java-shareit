package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable(value = "bookingId") Long bookingId,
                                     @RequestParam(value = "approved") Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable(value = "bookingId") Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getBookingByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getBooking(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingByOwner(userId, state);
    }
}