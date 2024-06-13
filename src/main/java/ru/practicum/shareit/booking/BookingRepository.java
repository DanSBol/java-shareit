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
    @Query(value = "select bo from Booking as bo where bo.booker = ?1 order by startDate desc")
    Page<Booking> getBookingByBooker(User booker, Pageable pageable);

    @Query(value = "select bo from Booking as bo join bo.item as it where it.owner = ?1 " +
            "order by bo.startDate desc")
    Page<Booking> getBookingByOwner(User owner, Pageable pageable);

    @Query(value = "select bo from Booking as bo where startDate <= CURRENT_TIMESTAMP() and " +
            "endDate >= CURRENT_TIMESTAMP() and bo.booker = ?1 " +
            "order by bo.startDate desc")
    Page<Booking> getBookingCurrentByBooker(User booker, Pageable pageable);

    @Query(value = "select bo from Booking as bo join bo.item as it where it.owner = ?1 and " +
            "bo.startDate <= CURRENT_TIMESTAMP() and bo.endDate >= CURRENT_TIMESTAMP() " +
            "order by bo.startDate desc")
    Page<Booking> getBookingCurrentByOwner(User owner, Pageable pageable);

    @Query(value = "select bo from Booking as bo where startDate > CURRENT_TIMESTAMP() and bo.booker = ?1 " +
            "order by bo.startDate desc")
    Page<Booking> getBookingFutureByBooker(User booker, Pageable pageable);

    @Query(value = "select bo from Booking as bo join bo.item as it where it.owner = ?1 and " +
            "bo.startDate > CURRENT_TIMESTAMP() " +
            "order by bo.startDate desc")
    Page<Booking> getBookingFutureByOwner(User owner, Pageable pageable);

    @Query(value = "select bo from Booking as bo where startDate > CURRENT_TIMESTAMP() and " +
            "bo.item = ?1 order by bo.startDate")
    List<Booking> getBookingOneFutureAllStatuses(Item item);

    @Query(value = "select bo from Booking as bo where startDate > CURRENT_TIMESTAMP() and " +
            "bo.item = ?1 and status='APPROVED' order by bo.startDate")
    List<Booking> getBookingOneFutureApproved(Item item);

    @Query(value = "select bo from Booking as bo where endDate < CURRENT_TIMESTAMP() and " +
            "status='APPROVED' and bo.booker = ?1 order by bo.startDate desc")
    Page<Booking> getBookingPastByBooker(User booker, Pageable pageable);

    @Query(value = "select bo from Booking as bo join bo.item as it where it.owner = ?1 and " +
            "bo.endDate < CURRENT_TIMESTAMP() and bo.status='APPROVED' order by bo.startDate desc")
    Page<Booking> getBookingPastByOwner(User owner, Pageable pageable);

    @Query(value = "select bo from Booking as bo where startDate <= CURRENT_TIMESTAMP() and " +
            "status='APPROVED' and bo.item = ?1 order by bo.endDate desc")
    List<Booking> getBookingOnePast(Item item);

    @Query(value = "select count(*) from Booking as bo where item = ?1 and " +
            "((?2 between startDate and endDate) or (?1 between startDate and endDate)) and status='APPROVED'")
    int getCount(long itemId, LocalDateTime start, LocalDateTime end);

    @Query(value = "select bo from Booking as bo where bo.item = ?1 and bo.booker = ?2 and " +
            "bo.status = 'APPROVED' and bo.endDate < CURRENT_TIMESTAMP() order by bo.startDate desc")
    List<Booking> getBookingByItemAndBooker(Item item, User booker);

    @Query(value = "select bo from Booking as bo where bo.booker = ?1 and bo.status = ?2 " +
            "order by bo.startDate desc")
    Page<Booking> getBookingByStatusAndBooker(User booker, BookingStatus status, Pageable pageable);

    @Query(value = "select bo from Booking as bo join bo.item as it where it.owner = ?1 " +
            "and bo.status = ?2 order by bo.startDate desc")
    Page<Booking> getBookingByStatusAndOwner(User owner, BookingStatus status, Pageable pageable);
}
