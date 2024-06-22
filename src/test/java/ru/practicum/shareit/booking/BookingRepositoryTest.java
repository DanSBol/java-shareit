package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {UserRepository.class, ItemRepository.class, CommentRepository.class,
                        BookingRepository.class}),
        showSql = false,
        properties = {
                "spring.datasource.url=jdbc:h2:./db/testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })

class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    public void setUp() {
        owner = new User();
        owner.setName("ownerName");
        owner.setEmail("ownerEmail@ya.ru");
        owner = userRepository.save(owner);

        item = new Item();
        item.setOwner(owner);
        item.setName("item name");
        item.setDescription("item description");
        item.setAvailable(true);
        item = itemRepository.save(item);

        booker = new User();
        booker.setName("bookerName");
        booker.setEmail("bookerEmail@ya.ru");
        booker = userRepository.save(booker);

        booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStartDate(LocalDateTime.now().plusSeconds(1));
        booking.setEndDate(LocalDateTime.now().plusSeconds(2));
        booking = bookingRepository.save(booking);
    }

    @AfterEach
    public void tearDown() {
        itemRepository.delete(item);
        userRepository.delete(owner);
        userRepository.delete(booker);
        bookingRepository.delete(booking);
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void givenBooking_whenSaved_thenCanBeFoundByIt() {
        Booking savedBooking = bookingRepository.findById(booking.getId()).orElse(null);
        assertNotNull(savedBooking);
        assertEquals(savedBooking.getBooker(), booker);
        assertEquals(savedBooking.getItem(), item);
        assertEquals(savedBooking.getStartDate(), booking.getStartDate());
        assertEquals(savedBooking.getEndDate(), booking.getEndDate());
    }

    @Test
    void givenBooking_whenUpdated_thenCanBeFoundByIdWithUpdatedData() {
        LocalDateTime newStartDate = booking.getStartDate().plusSeconds(1);
        LocalDateTime newEndDate = booking.getEndDate().plusSeconds(1);
        booking.setStartDate(newStartDate);
        booking.setEndDate(newEndDate);

        bookingRepository.save(booking);

        Booking updatedBooking = bookingRepository.findById(booking.getId())
                .orElse(null);

        assertNotNull(updatedBooking);
        assertEquals(newStartDate, updatedBooking.getStartDate());
        assertEquals(newEndDate, updatedBooking.getEndDate());
        assertEquals(booking.getItem(), updatedBooking.getItem());
        assertEquals(booking.getBooker(), updatedBooking.getBooker());
    }

    @Test
    void getBookingsByOwner() {
        User anotherOwner = new User();
        anotherOwner.setName("anotherOwnerName");
        anotherOwner.setEmail("anotherOwnerEmail@ya.ru");
        anotherOwner = userRepository.save(anotherOwner);

        Item anotherItem = new Item();
        anotherItem.setOwner(anotherOwner);
        anotherItem.setName("another item name");
        anotherItem.setDescription("another item description");
        anotherItem.setAvailable(true);
        anotherItem = itemRepository.save(anotherItem);

        User anotherBooker = new User();
        anotherBooker.setName("Another booker name");
        anotherBooker.setEmail("AnotherBookerEmail@ya.ru");
        anotherBooker = userRepository.save(anotherBooker);

        Booking anotherBooking = new Booking();
        anotherBooking.setStartDate(booking.getStartDate().plusSeconds(2));
        anotherBooking.setEndDate(booking.getEndDate().plusSeconds(2));
        anotherBooking.setBooker(anotherBooker);
        anotherBooking.setItem(anotherItem);
        bookingRepository.save(anotherBooking);

        Pageable pageable = PageRequest.of(0, 1);

        Page<Booking> bookings = bookingRepository.getBookingByOwner(owner, pageable);
        assertEquals(1, bookings.getSize());
        assertEquals(booking.getId(), bookings.getContent().get(0).getId());
        assertEquals(booking.getBooker(), bookings.getContent().get(0).getBooker());
        assertEquals(booking.getItem(), bookings.getContent().get(0).getItem());
        assertEquals(booking.getStartDate(), bookings.getContent().get(0).getStartDate());
        assertEquals(booking.getEndDate(), bookings.getContent().get(0).getEndDate());
    }

    @Test
    void getBookingsByBooker() {
        User anotherBooker = new User();
        anotherBooker.setName("Another booker name");
        anotherBooker.setEmail("AnotherBookerEmail@ya.ru");
        anotherBooker = userRepository.save(anotherBooker);

        Booking anotherBooking = new Booking();
        anotherBooking.setStartDate(booking.getStartDate().plusSeconds(2));
        anotherBooking.setEndDate(booking.getEndDate().plusSeconds(2));
        anotherBooking.setBooker(anotherBooker);
        anotherBooking.setItem(item);
        bookingRepository.save(anotherBooking);

        Pageable pageable = PageRequest.of(0, 1);

        Page<Booking> bookings = bookingRepository.getBookingByBooker(booker, pageable);
        assertEquals(1, bookings.getSize());
        assertEquals(booking.getId(), bookings.getContent().get(0).getId());
        assertEquals(booking.getBooker(), bookings.getContent().get(0).getBooker());
        assertEquals(booking.getItem(), bookings.getContent().get(0).getItem());
        assertEquals(booking.getStartDate(), bookings.getContent().get(0).getStartDate());
        assertEquals(booking.getEndDate(), bookings.getContent().get(0).getEndDate());
    }
}