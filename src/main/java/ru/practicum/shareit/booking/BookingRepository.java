package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select bo from Booking as bo order by startDate")
    Page<Booking> getBooking(Pageable pageable);

    @Query(value = "select bo from Booking as bo where startDate <= CURRENT_TIMESTAMP() and " +
            "endDate >= CURRENT_TIMESTAMP() order by startDate")
    Page<Booking> getBookingCurrent(Pageable pageable);

    @Query(value = "select bo from Booking as bo where startDate > CURRENT_TIMESTAMP() order by startDate")
    Page<Booking> getBookingFuture(Pageable pageable);

    @Query(value = "select bo from Booking as bo where startDate > CURRENT_TIMESTAMP() and " +
            "bo.item = ?1 order by startDate")
    List<Booking> getBookingOneFutureAllStatuses(Item item);

    @Query(value = "select bo from Booking as bo where startDate > CURRENT_TIMESTAMP() and " +
            "bo.item = ?1 and status='APPROVED' order by startDate")
    List<Booking> getBookingOneFutureApproved(Item item);

    @Query(value = "select bo from Booking as bo where endDate < CURRENT_TIMESTAMP() and " +
            "status='APPROVED' order by startDate")
    Page<Booking> getBookingPast(Pageable pageable);

    @Query(value = "select bo from Booking as bo where startDate <= CURRENT_TIMESTAMP() and " +
            "status='APPROVED' and bo.item = ?1 order by endDate desc")
    List<Booking> getBookingOnePast(Item item);

    @Query(value = "select count(*) from Booking as bo where item = ?1 and " +
            "((?2 between startDate and endDate) or (?1 between startDate and endDate)) and status='APPROVED'")
    int getCount(long itemId, LocalDateTime start, LocalDateTime end);

    @Query(value = "select bo from Booking as bo where bo.item = ?1 and bo.booker = ?2 and " +
            "bo.status = 'APPROVED' and bo.endDate < CURRENT_TIMESTAMP() order by startDate")
    List<Booking> getBookingByItemAndBooker(Item item, User booker);

    Page<Booking> getBookingByStatus(BookingStatus status, Pageable pageable);
}
