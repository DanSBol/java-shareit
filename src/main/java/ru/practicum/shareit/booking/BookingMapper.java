package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.item.ItemMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    public static Booking mapToBooking(BookingDto bookingDto, User user, Item item) {
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStartDate(LocalDateTime.parse(bookingDto.getStart()));
        booking.setEndDate(LocalDateTime.parse(bookingDto.getEnd()));
        booking.setStatus(bookingDto.getStatus() == null ? BookingStatus.WAITING :
                BookingStatus.valueOf(bookingDto.getStatus()));
        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto.BookingDtoBuilder()
            .id(booking.getId())
            .booker(UserMapper.mapToUserDto(booking.getBooker()))
            .itemId(booking.getItem().getId())
            .item(ItemMapper.mapToItemDto(booking.getItem(), null, null, null))
            .start(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).format(booking.getStartDate()))
            .end(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).format(booking.getEndDate()))
            .status(booking.getStatus().toString())
            .build();
    }

    public static BookingShotDto mapToBookingShotDto(Booking booking) {
        return new BookingShotDto.BookingShotDtoBuilder()
            .id(booking.getId())
            .bookerId(booking.getBooker().getId())
            .build();
    }

    public static List<BookingDto> mapToBookingsDto(List<Booking> bookings) {
        return bookings.stream()
            .map(BookingMapper::mapToBookingDto)
            .collect(Collectors.toList());
    }
}