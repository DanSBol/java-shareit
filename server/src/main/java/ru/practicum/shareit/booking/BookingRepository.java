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

    @Query(value = "select bo from Booking as bo where startDate <= ?2 and " +
            "endDate >= ?2 and bo.booker = ?1 " +
            "order by bo.startDate desc")
    Page<Booking> getBookingCurrentByBooker(User booker, LocalDateTime date, Pageable pageable);

    @Query(value = "select bo from Booking as bo join bo.item as it where it.owner = ?1 and " +
            "bo.startDate <= ?2 and bo.endDate >= ?2 " +
            "order by bo.startDate desc")
    Page<Booking> getBookingCurrentByOwner(User owner, LocalDateTime date, Pageable pageable);

    @Query(value = "select bo from Booking as bo where startDate > ?2 and bo.booker = ?1 " +
            "order by bo.startDate desc")
    Page<Booking> getBookingFutureByBooker(User booker, LocalDateTime date, Pageable pageable);

    @Query(value = "select bo from Booking as bo join bo.item as it where it.owner = ?1 and " +
            "bo.startDate > ?2 " +
            "order by bo.startDate desc")
    Page<Booking> getBookingFutureByOwner(User owner, LocalDateTime date, Pageable pageable);

    @Query(value = "select bo from Booking as bo where startDate > ?2 and " +
            "bo.item = ?1 order by bo.startDate")
    List<Booking> getBookingOneFutureAllStatuses(Item item, LocalDateTime date);

    @Query(value = "select bo from Booking as bo where startDate > ?2 and " +
            "bo.item = ?1 and status='APPROVED' order by bo.startDate")
    List<Booking> getBookingOneFutureApproved(Item item, LocalDateTime date);

    @Query(value = "select bo from Booking as bo where endDate < ?2 and " +
            "status='APPROVED' and bo.booker = ?1 order by bo.startDate desc")
    Page<Booking> getBookingPastByBooker(User booker, LocalDateTime date, Pageable pageable);

    @Query(value = "select bo from Booking as bo join bo.item as it where it.owner = ?1 and " +
            "bo.endDate < ?2 and bo.status='APPROVED' order by bo.startDate desc")
    Page<Booking> getBookingPastByOwner(User owner, LocalDateTime date, Pageable pageable);

    @Query(value = "select bo from Booking as bo where startDate <= ?2 and " +
            "status='APPROVED' and bo.item = ?1 order by bo.endDate desc")
    List<Booking> getBookingOnePast(Item item, LocalDateTime date);

    @Query(value = "select bo from Booking as bo where bo.item = ?1 and bo.booker = ?2 and " +
            "bo.status = 'APPROVED' and bo.endDate < ?3 order by bo.startDate desc")
    List<Booking> getBookingByItemAndBooker(Item item, User booker, LocalDateTime date);

    @Query(value = "select bo from Booking as bo where bo.booker = ?1 and bo.status = ?2 " +
            "order by bo.startDate desc")
    Page<Booking> getBookingByStatusAndBooker(User booker, BookingStatus status, Pageable pageable);

    @Query(value = "select bo from Booking as bo join bo.item as it where it.owner = ?1 " +
            "and bo.status = ?2 order by bo.startDate desc")
    Page<Booking> getBookingByStatusAndOwner(User owner, BookingStatus status, Pageable pageable);
}