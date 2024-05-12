package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select bo from Booking as bo order by startDate")
    List<Booking> getBooking();

    @Query(value = "select bo from Booking as bo where startDate <= 'now()' and endDate >= 'now()' order by startDate")
    List<Booking> getBookingCurrent();

    @Query(value = "select bo from Booking as bo where startDate > 'now()' order by startDate")
    List<Booking> getBookingFuture();

    @Query(value = "select bo from Booking as bo where startDate > 'now()' and bo.item = ?1 order by startDate")
    List<Booking> getBookingOneFutureAllStatuses(Item item);

    @Query(value = "select bo from Booking as bo where startDate > 'now()' and bo.item = ?1 and status='APPROVED'" +
            " order by startDate")
    List<Booking> getBookingOneFutureApproved(Item item);

    @Query(value = "select bo from Booking as bo where endDate < 'now()' and status='APPROVED' order by startDate")
    List<Booking> getBookingPast();

    @Query(value = "select bo from Booking as bo where startDate <= 'now()' and status='APPROVED' and bo.item = ?1 " +
            "order by endDate desc")
    List<Booking> getBookingOnePast(Item item);

    @Query(value = "select count(*) from Booking as bo where item = ?1 and " +
            "((?2 between startDate and endDate) or (?1 between startDate and endDate)) and status='APPROVED'")
    int getCount(long itemId, LocalDateTime start, LocalDateTime end);

    @Query(value = "select bo from Booking as bo where bo.item = ?1 and bo.booker = ?2 and " +
            "bo.status = 'APPROVED' and bo.endDate < 'now()' order by startDate")
    List<Booking> getBookingByItemAndBooker(Item item, User booker);

    List<Booking> getBookingByStatus(BookingStatus status);
}