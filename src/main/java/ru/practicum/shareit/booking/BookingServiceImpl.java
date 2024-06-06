package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        boolean isState = Arrays.stream(BookingStates.values()).anyMatch(element ->
                element.toString().equals(stringState));
        if (!isState) {
            throw new BadRequestException(String.format("Unknown state: %s", stringState));
        }
        BookingStates state = BookingStates.valueOf(stringState);
        Page<Booking> bookings = null;
        Pageable pageable = PageRequest.of(from, size);
        switch (state) {
            case ALL:
                bookings =  bookingRepository.getBookingByOwner(userId, pageable);
                break;
            case CURRENT:
                bookings =  bookingRepository.getBookingCurrentByOwner(userId, pageable);
                break;
            case FUTURE:
                bookings =  bookingRepository.getBookingFutureByOwner(userId, pageable);
                break;
            case PAST:
                bookings =  bookingRepository.getBookingPastByOwner(userId, pageable);
                break;
            case WAITING:
                bookings =  bookingRepository.getBookingByStatusAndOwner(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings =  bookingRepository.getBookingByStatusAndOwner(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                break;
        }
        return bookings.getContent().stream()
            .sorted((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate()))
            .map(BookingMapper::mapToBookingDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingByState(long userId, String stringState, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }
        boolean isState = Arrays.stream(BookingStates.values()).anyMatch(element ->
                element.toString().equals(stringState));
        if (!isState) {
            throw new BadRequestException(String.format("Unknown state: %s", stringState));
        }
        BookingStates state = BookingStates.valueOf(stringState);
        Page<Booking> bookings = null;
        Pageable pageable = PageRequest.of(from, size);
        switch (state) {
            case ALL:
                bookings =  bookingRepository.findAll(pageable);
                break;
            case CURRENT:
                bookings =  bookingRepository.getBookingCurrent(pageable);
                break;
            case FUTURE:
                bookings =  bookingRepository.getBookingFuture(pageable);
                break;
            case PAST:
                bookings =  bookingRepository.getBookingPast(pageable);
                break;
            case WAITING:
                bookings =  bookingRepository.getBookingByStatus(BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings =  bookingRepository.getBookingByStatus(BookingStatus.REJECTED, pageable);
                break;
            default:
                break;
        }
        return bookings.getContent().stream()
            .sorted((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate()))
            .map(BookingMapper::mapToBookingDto)
            .collect(Collectors.toList());
    }
}