package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.config.PersistenceConfig;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = { "db.name=test"})
@SpringJUnitConfig({PersistenceConfig.class, UserServiceImpl.class, ItemServiceImpl.class, BookingServiceImpl.class})
class BookingServiceImplTest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @Test
    void addBooking() throws InterruptedException {
        // given & when
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        BookingDto bookingDto = makeBookingDto(bookerDto, itemDto, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2));
        BookingDto getBookingDto = bookingService.addBooking(bookerDto.getId(), bookingDto);

        assertThat(getBookingDto.getId(), notNullValue());
        assertThat(getBookingDto.getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(getBookingDto.getItem(), equalTo(bookingDto.getItem()));
        assertThat(getBookingDto.getStart(), equalTo(bookingDto.getStart()));
        assertThat(getBookingDto.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(getBookingDto.getStatus(), equalTo(BookingStatus.WAITING.toString()));

        // then
        TypedQuery<User> userQuery = em.createQuery("Select us from User us where us.id = :id", User.class);
        User booker = userQuery.setParameter("id", bookerDto.getId())
                .getSingleResult();

        TypedQuery<Item> itemQuery = em.createQuery("Select it from Item it where it.id = :id", Item.class);
        Item item = itemQuery.setParameter("id", itemDto.getId())
                .getSingleResult();

        TypedQuery<Booking> bookingQuery = em.createQuery("Select bo from Booking bo " +
                "join Item it on bo.item = it.id join User us on bo.booker = us.id where bo.id = :id", Booking.class);
        Booking booking = bookingQuery.setParameter("id", getBookingDto.getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getBooker(), equalTo(booker));
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getStartDate(), equalTo(LocalDateTime.parse(getBookingDto.getStart())));
        assertThat(booking.getEndDate(), equalTo(LocalDateTime.parse(getBookingDto.getEnd())));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void addBooking_400_wrong_dates() {
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        BookingDto bookingDto = makeBookingDto(bookerDto, itemDto, LocalDateTime.now().plusSeconds(2),
                LocalDateTime.now().plusSeconds(1));
        assertThatThrownBy(() -> bookingService.addBooking(1L, bookingDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Wrong dates.");
    }

    @Test
    void addBooking_400_item_unavailable() {
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", false));
        BookingDto bookingDto = makeBookingDto(bookerDto, itemDto, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2));
        assertThatThrownBy(() -> bookingService.addBooking(1L, bookingDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Item is unavailable.");
    }

    @Test
    void addBooking_400_owner_booker_equal() {
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        BookingDto bookingDto = makeBookingDto(bookerDto, itemDto, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2));
        assertThatThrownBy(() -> bookingService.addBooking(1L, bookingDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found.");
    }

    @Test
    void approveBooking_approved() throws InterruptedException {
        // given & when
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        BookingDto bookingDto = makeBookingDto(bookerDto, itemDto, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2));
        bookingDto = bookingService.addBooking(bookerDto.getId(), bookingDto);
        bookingDto = bookingService.approveBooking(ownerDto.getId(), bookingDto.getId(), true);

        // then
        TypedQuery<User> userQuery = em.createQuery("Select us from User us where us.id = :id", User.class);
        User booker = userQuery.setParameter("id", bookerDto.getId())
                .getSingleResult();

        TypedQuery<Item> itemQuery = em.createQuery("Select it from Item it where it.id = :id", Item.class);
        Item item = itemQuery.setParameter("id", itemDto.getId())
                .getSingleResult();

        TypedQuery<Booking> bookingQuery = em.createQuery("Select bo from Booking bo " +
                "join Item it on bo.item = it.id join User us on bo.booker = us.id where bo.id = :id", Booking.class);
        Booking booking = bookingQuery.setParameter("id", bookingDto.getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getBooker(), equalTo(booker));
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getStartDate(), equalTo(LocalDateTime.parse(bookingDto.getStart())));
        assertThat(booking.getEndDate(), equalTo(LocalDateTime.parse(bookingDto.getEnd())));
        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void approveBooking_rejected() throws InterruptedException {
        // given & when
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        BookingDto bookingDto = makeBookingDto(bookerDto, itemDto, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2));
        bookingDto = bookingService.addBooking(bookerDto.getId(), bookingDto);
        bookingDto = bookingService.approveBooking(ownerDto.getId(), bookingDto.getId(), false);

        // then
        TypedQuery<User> userQuery = em.createQuery("Select us from User us where us.id = :id", User.class);
        User booker = userQuery.setParameter("id", bookerDto.getId())
                .getSingleResult();

        TypedQuery<Item> itemQuery = em.createQuery("Select it from Item it where it.id = :id", Item.class);
        Item item = itemQuery.setParameter("id", itemDto.getId())
                .getSingleResult();

        TypedQuery<Booking> bookingQuery = em.createQuery("Select bo from Booking bo " +
                "join Item it on bo.item = it.id join User us on bo.booker = us.id where bo.id = :id", Booking.class);
        Booking booking = bookingQuery.setParameter("id", bookingDto.getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getBooker(), equalTo(booker));
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getStartDate(), equalTo(LocalDateTime.parse(bookingDto.getStart())));
        assertThat(booking.getEndDate(), equalTo(LocalDateTime.parse(bookingDto.getEnd())));
        assertThat(booking.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void approveBooking_404_user_not_found() {
        assertThatThrownBy(() -> bookingService.approveBooking(1L, 1L, true))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found.");
    }

    @Test
    void getBooking() throws InterruptedException {
        // given & when
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        BookingDto bookingDto = makeBookingDto(bookerDto, itemDto, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2));
        bookingDto = bookingService.addBooking(bookerDto.getId(), bookingDto);
        BookingDto getBookingDto = bookingService.getBooking(ownerDto.getId(), bookingDto.getId());

        // then
        TypedQuery<User> userQuery = em.createQuery("Select us from User us where us.id = :id", User.class);
        User booker = userQuery.setParameter("id", bookerDto.getId())
                .getSingleResult();

        TypedQuery<Item> itemQuery = em.createQuery("Select it from Item it where it.id = :id", Item.class);
        Item item = itemQuery.setParameter("id", itemDto.getId())
                .getSingleResult();

        TypedQuery<Booking> bookingQuery = em.createQuery("Select bo from Booking bo " +
                "join Item it on bo.item = it.id join User us on bo.booker = us.id where bo.id = :id", Booking.class);
        Booking booking = bookingQuery.setParameter("id", bookingDto.getId())
                .getSingleResult();

        assertThat(getBookingDto.getId(), equalTo(bookingDto.getId()));
        assertThat(getBookingDto.getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(getBookingDto.getItem(), equalTo(bookingDto.getItem()));
        assertThat(getBookingDto.getStart(), equalTo(bookingDto.getStart()));
        assertThat(getBookingDto.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(getBookingDto.getStatus(), equalTo(bookingDto.getStatus()));
    }

    @Test
    void getBooking_404_user_not_found() {
        assertThatThrownBy(() -> bookingService.getBooking(1L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found.");
    }

    @Test
    void getBookingByOwner_All() {
        // given & when
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto firstBookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        UserDto secondBookerDto = userService.addUser(makeUserDto("Alexander", "alexander@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        List<BookingDto> sourceBookingDto = List.of(
                makeBookingDto(firstBookerDto, itemDto, LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2)),
                makeBookingDto(secondBookerDto, itemDto, LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3))
        );
        List<BookingDto> bookingDto = List.of(
                bookingService.addBooking(firstBookerDto.getId(), sourceBookingDto.get(0)),
                bookingService.addBooking(secondBookerDto.getId(), sourceBookingDto.get(1))
        );

        List<BookingDto> getBookingDto = bookingService.getBookingByOwner(ownerDto.getId(), "ALL", 1, 1);

        assertThat(getBookingDto.size(), equalTo(1));
        assertThat(getBookingDto.get(0).getId(), equalTo(bookingDto.get(0).getId()));
        assertThat(getBookingDto.get(0).getBooker(), equalTo(bookingDto.get(0).getBooker()));
        assertThat(getBookingDto.get(0).getItemId(), equalTo(bookingDto.get(0).getItemId()));
        assertThat(getBookingDto.get(0).getItem(), equalTo(bookingDto.get(0).getItem()));
        assertThat(getBookingDto.get(0).getStart(), equalTo(bookingDto.get(0).getStart()));
        assertThat(getBookingDto.get(0).getEnd(), equalTo(bookingDto.get(0).getEnd()));
        assertThat(getBookingDto.get(0).getStatus(), equalTo(bookingDto.get(0).getStatus()));
    }

    @Test
    void getBookingByOwner_Current() {
        // given & when
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto firstBookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        UserDto secondBookerDto = userService.addUser(makeUserDto("Alexander", "alexander@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        List<BookingDto> sourceBookingDto = List.of(
                makeBookingDto(firstBookerDto, itemDto, LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2)),
                makeBookingDto(secondBookerDto, itemDto, LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3))
        );
        List<BookingDto> bookingDto = List.of(
                bookingService.addBooking(firstBookerDto.getId(), sourceBookingDto.get(0)),
                bookingService.addBooking(secondBookerDto.getId(), sourceBookingDto.get(1))
        );

        List<BookingDto> getBookingDto = bookingService.getBookingByOwner(ownerDto.getId(), "CURRENT", 1, 1);

        assertThat(getBookingDto.size(), equalTo(0));
    }

    @Test
    void getBookingByOwner_Future() {
        // given & when
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto firstBookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        UserDto secondBookerDto = userService.addUser(makeUserDto("Alexander", "alexander@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        List<BookingDto> sourceBookingDto = List.of(
                makeBookingDto(firstBookerDto, itemDto, LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2)),
                makeBookingDto(secondBookerDto, itemDto, LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3))
        );
        List<BookingDto> bookingDto = List.of(
                bookingService.addBooking(firstBookerDto.getId(), sourceBookingDto.get(0)),
                bookingService.addBooking(secondBookerDto.getId(), sourceBookingDto.get(1))
        );

        List<BookingDto> getBookingDto = bookingService.getBookingByOwner(ownerDto.getId(), "FUTURE", 1, 1);

        assertThat(getBookingDto.size(), equalTo(1));
        assertThat(getBookingDto.get(0).getId(), equalTo(bookingDto.get(0).getId()));
        assertThat(getBookingDto.get(0).getBooker(), equalTo(bookingDto.get(0).getBooker()));
        assertThat(getBookingDto.get(0).getItemId(), equalTo(bookingDto.get(0).getItemId()));
        assertThat(getBookingDto.get(0).getItem(), equalTo(bookingDto.get(0).getItem()));
        assertThat(getBookingDto.get(0).getStart(), equalTo(bookingDto.get(0).getStart()));
        assertThat(getBookingDto.get(0).getEnd(), equalTo(bookingDto.get(0).getEnd()));
        assertThat(getBookingDto.get(0).getStatus(), equalTo(bookingDto.get(0).getStatus()));
    }

    @Test
    void getBookingByOwner_Past() {
        // given & when
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto firstBookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        UserDto secondBookerDto = userService.addUser(makeUserDto("Alexander", "alexander@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        List<BookingDto> sourceBookingDto = List.of(
                makeBookingDto(firstBookerDto, itemDto, LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2)),
                makeBookingDto(secondBookerDto, itemDto, LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3))
        );
        List<BookingDto> bookingDto = List.of(
                bookingService.addBooking(firstBookerDto.getId(), sourceBookingDto.get(0)),
                bookingService.addBooking(secondBookerDto.getId(), sourceBookingDto.get(1))
        );

        List<BookingDto> getBookingDto = bookingService.getBookingByOwner(ownerDto.getId(), "PAST", 1, 1);

        assertThat(getBookingDto.size(), equalTo(0));
    }

    @Test
    void getBookingByOwner_Waiting() {
        // given & when
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto firstBookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        UserDto secondBookerDto = userService.addUser(makeUserDto("Alexander", "alexander@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        List<BookingDto> sourceBookingDto = List.of(
                makeBookingDto(firstBookerDto, itemDto, LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2)),
                makeBookingDto(secondBookerDto, itemDto, LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3))
        );
        List<BookingDto> bookingDto = List.of(
                bookingService.addBooking(firstBookerDto.getId(), sourceBookingDto.get(0)),
                bookingService.addBooking(secondBookerDto.getId(), sourceBookingDto.get(1))
        );

        List<BookingDto> getBookingDto = bookingService.getBookingByOwner(ownerDto.getId(), "WAITING", 1, 1);

        assertThat(getBookingDto.size(), equalTo(1));
        assertThat(getBookingDto.get(0).getId(), equalTo(bookingDto.get(0).getId()));
        assertThat(getBookingDto.get(0).getBooker(), equalTo(bookingDto.get(0).getBooker()));
        assertThat(getBookingDto.get(0).getItemId(), equalTo(bookingDto.get(0).getItemId()));
        assertThat(getBookingDto.get(0).getItem(), equalTo(bookingDto.get(0).getItem()));
        assertThat(getBookingDto.get(0).getStart(), equalTo(bookingDto.get(0).getStart()));
        assertThat(getBookingDto.get(0).getEnd(), equalTo(bookingDto.get(0).getEnd()));
        assertThat(getBookingDto.get(0).getStatus(), equalTo(bookingDto.get(0).getStatus()));
    }

    @Test
    void getBookingByOwner_Rejected() {
        // given & when
        UserDto ownerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto firstBookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        UserDto secondBookerDto = userService.addUser(makeUserDto("Alexander", "alexander@ya.ru"));
        ItemDto itemDto = itemService.addItem(ownerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        List<BookingDto> sourceBookingDto = List.of(
                makeBookingDto(firstBookerDto, itemDto, LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2)),
                makeBookingDto(secondBookerDto, itemDto, LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3))
        );
        List<BookingDto> bookingDto = List.of(
                bookingService.addBooking(firstBookerDto.getId(), sourceBookingDto.get(0)),
                bookingService.addBooking(secondBookerDto.getId(), sourceBookingDto.get(1))
        );

        List<BookingDto> getBookingDto = bookingService.getBookingByOwner(ownerDto.getId(), "REJECTED", 1, 1);

        assertThat(getBookingDto.size(), equalTo(0));
    }

    @Test
    void getBookingByOwner_404_user_not_found() {
        assertThatThrownBy(() -> bookingService.getBookingByOwner(1L, "ALL", 0, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found.");
    }

    @Test
    void getBookingByBooker_All() {
        // given & when
        UserDto firstOwnerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto secondOwnerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Alexander", "alexander@ya.ru"));
        ItemDto firstItemDto = itemService.addItem(firstOwnerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        ItemDto secondItemDto = itemService.addItem(secondOwnerDto.getId(),
                makeItemDto("TV", "Large color TV", true));
        List<BookingDto> sourceBookingDto = List.of(
                makeBookingDto(bookerDto, firstItemDto, LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2)),
                makeBookingDto(bookerDto, secondItemDto, LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3))
        );
        List<BookingDto> bookingDto = List.of(
                bookingService.addBooking(bookerDto.getId(), sourceBookingDto.get(0)),
                bookingService.addBooking(bookerDto.getId(), sourceBookingDto.get(1))
        );
        List<BookingDto> getBookingDto = bookingService.getBookingByBooker(bookerDto.getId(), "ALL", 1, 1);

        assertThat(getBookingDto.size(), equalTo(1));
        assertThat(getBookingDto.get(0).getId(), equalTo(bookingDto.get(0).getId()));
        assertThat(getBookingDto.get(0).getBooker(), equalTo(bookingDto.get(0).getBooker()));
        assertThat(getBookingDto.get(0).getItemId(), equalTo(bookingDto.get(0).getItemId()));
        assertThat(getBookingDto.get(0).getItem(), equalTo(bookingDto.get(0).getItem()));
        assertThat(getBookingDto.get(0).getStart(), equalTo(bookingDto.get(0).getStart()));
        assertThat(getBookingDto.get(0).getEnd(), equalTo(bookingDto.get(0).getEnd()));
        assertThat(getBookingDto.get(0).getStatus(), equalTo(bookingDto.get(0).getStatus()));
    }

    @Test
    void getBookingByBooker_Current() {
        // given & when
        UserDto firstOwnerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto secondOwnerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Alexander", "alexander@ya.ru"));
        ItemDto firstItemDto = itemService.addItem(firstOwnerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        ItemDto secondItemDto = itemService.addItem(secondOwnerDto.getId(),
                makeItemDto("TV", "Large color TV", true));
        List<BookingDto> sourceBookingDto = List.of(
                makeBookingDto(bookerDto, firstItemDto, LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2)),
                makeBookingDto(bookerDto, secondItemDto, LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3))
        );
        List<BookingDto> bookingDto = List.of(
                bookingService.addBooking(bookerDto.getId(), sourceBookingDto.get(0)),
                bookingService.addBooking(bookerDto.getId(), sourceBookingDto.get(1))
        );
        List<BookingDto> getBookingDto = bookingService.getBookingByBooker(bookerDto.getId(), "CURRENT", 1, 1);

        assertThat(getBookingDto.size(), equalTo(0));
    }

    @Test
    void getBookingByBooker_Future() {
        // given & when
        UserDto firstOwnerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto secondOwnerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Alexander", "alexander@ya.ru"));
        ItemDto firstItemDto = itemService.addItem(firstOwnerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        ItemDto secondItemDto = itemService.addItem(secondOwnerDto.getId(),
                makeItemDto("TV", "Large color TV", true));
        List<BookingDto> sourceBookingDto = List.of(
                makeBookingDto(bookerDto, firstItemDto, LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2)),
                makeBookingDto(bookerDto, secondItemDto, LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3))
        );
        List<BookingDto> bookingDto = List.of(
                bookingService.addBooking(bookerDto.getId(), sourceBookingDto.get(0)),
                bookingService.addBooking(bookerDto.getId(), sourceBookingDto.get(1))
        );
        List<BookingDto> getBookingDto = bookingService.getBookingByBooker(bookerDto.getId(), "FUTURE", 1, 1);

        assertThat(getBookingDto.size(), equalTo(1));
        assertThat(getBookingDto.get(0).getId(), equalTo(bookingDto.get(0).getId()));
        assertThat(getBookingDto.get(0).getBooker(), equalTo(bookingDto.get(0).getBooker()));
        assertThat(getBookingDto.get(0).getItemId(), equalTo(bookingDto.get(0).getItemId()));
        assertThat(getBookingDto.get(0).getItem(), equalTo(bookingDto.get(0).getItem()));
        assertThat(getBookingDto.get(0).getStart(), equalTo(bookingDto.get(0).getStart()));
        assertThat(getBookingDto.get(0).getEnd(), equalTo(bookingDto.get(0).getEnd()));
        assertThat(getBookingDto.get(0).getStatus(), equalTo(bookingDto.get(0).getStatus()));
    }

    @Test
    void getBookingByBooker_Past() {
        // given & when
        UserDto firstOwnerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto secondOwnerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Alexander", "alexander@ya.ru"));
        ItemDto firstItemDto = itemService.addItem(firstOwnerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        ItemDto secondItemDto = itemService.addItem(secondOwnerDto.getId(),
                makeItemDto("TV", "Large color TV", true));
        List<BookingDto> sourceBookingDto = List.of(
                makeBookingDto(bookerDto, firstItemDto, LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2)),
                makeBookingDto(bookerDto, secondItemDto, LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3))
        );
        List<BookingDto> bookingDto = List.of(
                bookingService.addBooking(bookerDto.getId(), sourceBookingDto.get(0)),
                bookingService.addBooking(bookerDto.getId(), sourceBookingDto.get(1))
        );
        List<BookingDto> getBookingDto = bookingService.getBookingByBooker(bookerDto.getId(), "PAST", 1, 1);

        assertThat(getBookingDto.size(), equalTo(0));
    }

    @Test
    void getBookingByBooker_Waiting() {
        // given & when
        UserDto firstOwnerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto secondOwnerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Alexander", "alexander@ya.ru"));
        ItemDto firstItemDto = itemService.addItem(firstOwnerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        ItemDto secondItemDto = itemService.addItem(secondOwnerDto.getId(),
                makeItemDto("TV", "Large color TV", true));
        List<BookingDto> sourceBookingDto = List.of(
                makeBookingDto(bookerDto, firstItemDto, LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2)),
                makeBookingDto(bookerDto, secondItemDto, LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3))
        );
        List<BookingDto> bookingDto = List.of(
                bookingService.addBooking(bookerDto.getId(), sourceBookingDto.get(0)),
                bookingService.addBooking(bookerDto.getId(), sourceBookingDto.get(1))
        );
        List<BookingDto> getBookingDto = bookingService.getBookingByBooker(bookerDto.getId(), "WAITING", 1, 1);

        assertThat(getBookingDto.size(), equalTo(1));
        assertThat(getBookingDto.get(0).getId(), equalTo(bookingDto.get(0).getId()));
        assertThat(getBookingDto.get(0).getBooker(), equalTo(bookingDto.get(0).getBooker()));
        assertThat(getBookingDto.get(0).getItemId(), equalTo(bookingDto.get(0).getItemId()));
        assertThat(getBookingDto.get(0).getItem(), equalTo(bookingDto.get(0).getItem()));
        assertThat(getBookingDto.get(0).getStart(), equalTo(bookingDto.get(0).getStart()));
        assertThat(getBookingDto.get(0).getEnd(), equalTo(bookingDto.get(0).getEnd()));
        assertThat(getBookingDto.get(0).getStatus(), equalTo(bookingDto.get(0).getStatus()));
    }

    @Test
    void getBookingByBooker_Rejected() {
        // given & when
        UserDto firstOwnerDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto secondOwnerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Alexander", "alexander@ya.ru"));
        ItemDto firstItemDto = itemService.addItem(firstOwnerDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        ItemDto secondItemDto = itemService.addItem(secondOwnerDto.getId(),
                makeItemDto("TV", "Large color TV", true));
        List<BookingDto> sourceBookingDto = List.of(
                makeBookingDto(bookerDto, firstItemDto, LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2)),
                makeBookingDto(bookerDto, secondItemDto, LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3))
        );
        List<BookingDto> bookingDto = List.of(
                bookingService.addBooking(bookerDto.getId(), sourceBookingDto.get(0)),
                bookingService.addBooking(bookerDto.getId(), sourceBookingDto.get(1))
        );
        List<BookingDto> getBookingDto = bookingService.getBookingByBooker(bookerDto.getId(), "REJECTED", 1, 1);

        assertThat(getBookingDto.size(), equalTo(0));
    }

    @Test
    void getBookingByBooker_404_user_not_found() {
        assertThatThrownBy(() -> bookingService.getBookingByBooker(1L, "ALL", 0, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found.");
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }

    private BookingDto makeBookingDto(UserDto booker, ItemDto itemDto, LocalDateTime startDate, LocalDateTime endDate) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setBooker(booker);
        bookingDto.setItem(itemDto);
        bookingDto.setItemId(itemDto.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        bookingDto.setStart(startDate.format(formatter));
        bookingDto.setEnd(endDate.format(formatter));
        return bookingDto;
    }
}