package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.item.ItemMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setBooker(UserMapper.mapToUserDto(booking.getBooker()));
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setItem(ItemMapper.mapToItemDto(booking.getItem(), null, null, null));
        bookingDto.setStart(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).format(booking.getStartDate()));
        bookingDto.setEnd(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).format(booking.getEndDate()));
        bookingDto.setStatus(booking.getStatus().toString());
        return bookingDto;
    }

    public static BookingShotDto mapToBookingShotDto(Booking booking) {
        BookingShotDto bookingShotDto = new BookingShotDto();
        bookingShotDto.setId(booking.getId());
        bookingShotDto.setBookerId(booking.getBooker().getId());
        return bookingShotDto;
    }
}