package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BadRequestException;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
										     @RequestBody @Valid BookingDto bookingDto) {
		log.info("Creating booking {}, userId={}", bookingDto, userId);
		return bookingClient.addBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
												 @PathVariable Long bookingId,
												 @RequestParam Boolean approved) {
		log.info("Approving booking {}, userId={}, approved={}", bookingId, userId, approved);
		return bookingClient.approveBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookingByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.fromString(stateParam)
				.orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
		log.info("Get booking by booker with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookingsByBooker(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
													 @RequestParam(name = "state",
															 defaultValue = "all") String stateParam,
													 @PositiveOrZero @RequestParam(name = "from",
															 defaultValue = "0") Integer from,
													 @Positive @RequestParam(name = "size",
															 defaultValue = "10") Integer size) {
		BookingState state = BookingState.fromString(stateParam)
				.orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
		log.info("Get booking by owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookingsByOwner(userId, state, from, size);
	}
}