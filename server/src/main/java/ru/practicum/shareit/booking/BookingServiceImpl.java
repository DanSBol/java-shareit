package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto addBooking(long userId, BookingDto bookingDto) {
        if ((bookingDto.getStart() == null || bookingDto.getEnd() == null) ||
                !LocalDateTime.parse(bookingDto.getStart()).isBefore(LocalDateTime.parse(bookingDto.getEnd())) ||
                LocalDate.now().isAfter(ChronoLocalDate.from(LocalDateTime.parse(bookingDto.getStart())))) {
            throw new BadRequestException("Wrong dates.");
        }
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Item not found."));
        if (!item.getAvailable()) {
            throw new BadRequestException("Item is unavailable.");
        }
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        Booking booking = BookingMapper.mapToBooking(bookingDto, booker, item);
        if (Objects.equals(booker.getId(), item.getOwner().getId())) {
            throw new NotFoundException("Item owner and booker are equal.");
        }
        return BookingMapper.mapToBookingDto(bookingRepository.saveAndFlush(booking));
    }

    @Transactional
    @Override
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking not found."));
        Item item = booking.getItem();
        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("This item has another owner.");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BadRequestException("Booking is already approved.");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.mapToBookingDto(bookingRepository.saveAndFlush(booking));
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking not found"));
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId)  {
            throw new NotFoundException("This item has another owner or another booker.");
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingByOwner(long userId, String stringState, int from, int size) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        boolean isState = Arrays.stream(BookingStates.values()).anyMatch(element ->
                element.toString().equals(stringState));
        if (!isState) {
            throw new BadRequestException(String.format("Unknown state: %s", stringState));
        }
        BookingStates state = BookingStates.valueOf(stringState);
        Page<Booking> bookings = null;
        checkFromSize(from, size);
        PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        switch (state) {
            case ALL:
                bookings =  bookingRepository.getBookingByOwner(owner, pageable);
                break;
            case CURRENT:
                bookings =  bookingRepository.getBookingCurrentByOwner(owner, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings =  bookingRepository.getBookingFutureByOwner(owner, LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookings =  bookingRepository.getBookingPastByOwner(owner, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings =  bookingRepository.getBookingByStatusAndOwner(owner, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings =  bookingRepository.getBookingByStatusAndOwner(owner, BookingStatus.REJECTED, pageable);
                break;
            default:
                break;
        }
        return bookings.getContent().stream()
            .map(BookingMapper::mapToBookingDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingByBooker(long userId, String stringState, int from, int size) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        boolean isState = Arrays.stream(BookingStates.values()).anyMatch(element ->
                element.toString().equals(stringState));
        if (!isState) {
            throw new BadRequestException(String.format("Unknown state: %s", stringState));
        }
        BookingStates state = BookingStates.valueOf(stringState);
        Page<Booking> bookings = null;
        checkFromSize(from, size);
        PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        switch (state) {
            case ALL:
                bookings =  bookingRepository.getBookingByBooker(booker, pageable);
                break;
            case CURRENT:
                bookings =  bookingRepository.getBookingCurrentByBooker(booker, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings =  bookingRepository.getBookingFutureByBooker(booker, LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookings =  bookingRepository.getBookingPastByBooker(booker, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings =  bookingRepository.getBookingByStatusAndBooker(booker, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings =  bookingRepository.getBookingByStatusAndBooker(booker, BookingStatus.REJECTED, pageable);
                break;
            default:
                break;
        }

        return bookings.getContent().stream()
            .map(BookingMapper::mapToBookingDto)
            .collect(Collectors.toList());
    }

    private void checkFromSize(int from, int size) {
        if (from < 0 & size < 0) {
            throw new BadRequestException(String.format("Negative from(%d) and size(%d)", from, size));
        }
        if (from < 0) {
            throw new BadRequestException(String.format("Negative from: %d", from));
        }
        if (size < 0) {
            throw new BadRequestException(String.format("Negative size: %d", size));
        }
    }
}
