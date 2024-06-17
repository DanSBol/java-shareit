package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.config.PersistenceConfig;
import ru.practicum.shareit.user.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = { "db.name=test"})
@SpringJUnitConfig({PersistenceConfig.class, UserServiceImpl.class, ItemServiceImpl.class, BookingServiceImpl.class})
class ItemServiceImplTest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @Test
    void addItem() {
        // given & when
        UserDto userDto = makeUserDto("Alexey", "alexey@ya.ru");
        userDto = userService.addUser(userDto);
        ItemDto itemDto = makeItemDto("Microwave oven",
                "Power compact microwave oven", true);
        itemDto = itemService.addItem(userDto.getId(), itemDto);

        // then
        TypedQuery<User> userQuery = em.createQuery("Select us from User us where us.email = :email",
                User.class);
        User user = userQuery.setParameter("email", userDto.getEmail())
                .getSingleResult();
        TypedQuery<Item> itemQuery = em.createQuery("Select it from Item it join User us on it.owner = us.id " +
                "where us.email = :email", Item.class);
        Item item = itemQuery.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getOwner(), equalTo(user));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void updateItem() {
        // given & when
        UserDto userDto = makeUserDto("Alexey", "alexey@ya.ru");
        userDto = userService.addUser(userDto);
        ItemDto itemDto = makeItemDto("Microwave oven",
                "Power compact microwave oven", true);
        itemDto = itemService.addItem(userDto.getId(), itemDto);
        ItemDto updatedItemDto = makeItemDto("Microwave oven",
                "Non-working oven", false);

        itemDto = itemService.updateItem(userDto.getId(), itemDto.getId(), updatedItemDto);

        // then
        TypedQuery<User> userQuery = em.createQuery("Select us from User us where us.email = :email",
                User.class);
        User user = userQuery.setParameter("email", userDto.getEmail())
                .getSingleResult();
        TypedQuery<Item> itemQuery = em.createQuery("Select it from Item it join User us on it.owner = us.id " +
                "where us.email = :email", Item.class);
        Item item = itemQuery.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getOwner(), equalTo(user));
        assertThat(item.getName(), equalTo(updatedItemDto.getName()));
        assertThat(item.getDescription(), equalTo(updatedItemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(updatedItemDto.getAvailable()));
    }

    @Test
    void deleteItem() {
        // given & when
        UserDto userDto = makeUserDto("Alexey", "alexey@ya.ru");
        userDto = userService.addUser(userDto);
        ItemDto itemDto = makeItemDto("Microwave oven",
                "Power compact microwave oven", true);
        itemDto = itemService.addItem(userDto.getId(), itemDto);
        ItemDto updatedItemDto = makeItemDto("Microwave oven",
                "Non-working oven", false);
        itemService.deleteItem(userDto.getId(), itemDto.getId());

        // then
        TypedQuery<User> userQuery = em.createQuery("Select us from User us where us.email = :email",
                User.class);
        User user = userQuery.setParameter("email", userDto.getEmail())
                .getSingleResult();
        TypedQuery<Item> itemQuery = em.createQuery("Select it from Item it join User us on it.owner = us.id " +
                "where us.email = :email", Item.class);
        List<Item> items = itemQuery.setParameter("email", userDto.getEmail())
                .getResultList();

        assertThat(String.valueOf(items.isEmpty()), true);
    }

    @Test
    void getItem() {
        // given & when
        UserDto userDto = makeUserDto("Alexey", "alexey@ya.ru");
        userDto = userService.addUser(userDto);
        UserDto bookerDto = makeUserDto("Ilya", "ilya@ya.ru");
        bookerDto = userService.addUser(bookerDto);
        ItemDto itemDto = makeItemDto("Microwave oven",
                "Power compact microwave oven", true);
        itemDto = itemService.addItem(userDto.getId(), itemDto);

        BookingDto lastBookingDto = makeBookingDto(bookerDto, itemDto, LocalDateTime.now().minusSeconds(2),
                LocalDateTime.now().minusSeconds(1));
        lastBookingDto = bookingService.addBooking(bookerDto.getId(), lastBookingDto);
        bookingService.approveBooking(userDto.getId(), lastBookingDto.getId(), true);
        BookingDto nextBookingDto = makeBookingDto(bookerDto, itemDto, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(1).plusSeconds(1));
        nextBookingDto = bookingService.addBooking(bookerDto.getId(), nextBookingDto);
        bookingService.approveBooking(userDto.getId(), nextBookingDto.getId(), true);

        ItemDto getItemDto = itemService.getItem(userDto.getId(), itemDto.getId());

        TypedQuery<Booking> bookingQuery = em.createQuery("Select bo from Booking bo where bo.id = :id",
                Booking.class);
        Booking lastBooking = bookingQuery.setParameter("id", lastBookingDto.getId())
                .getSingleResult();
        Booking nextBooking = bookingQuery.setParameter("id", nextBookingDto.getId())
                .getSingleResult();
        BookingShotDto lastBookingShotDto = BookingMapper.mapToBookingShotDto(lastBooking);
        BookingShotDto nextBookingShotDto = BookingMapper.mapToBookingShotDto(nextBooking);

        assertThat(getItemDto.getId(), notNullValue());
        assertThat(itemDto.getUserId(), equalTo(getItemDto.getUserId()));
        assertThat(itemDto.getName(), equalTo(getItemDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(getItemDto.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(getItemDto.getAvailable()));
        assertThat(lastBookingShotDto, equalTo(getItemDto.getLastBooking()));
        assertThat(nextBookingShotDto, equalTo(getItemDto.getNextBooking()));
    }

    @Test
    void getItemsByOwner() {
        // given & when
        UserDto userDto = makeUserDto("Alexey", "alexey@ya.ru");
        userDto = userService.addUser(userDto);

        List<ItemDto> sourceItems = List.of(
                makeItemDto("Microwave oven", "Power compact microwave oven", true),
                makeItemDto("TV", "Large color TV", false)
        );

        itemService.addItem(userDto.getId(), sourceItems.get(0));
        itemService.addItem(userDto.getId(), sourceItems.get(1));

        List<ItemDto> items = itemService.getItemsByOwner(userDto.getId(), 1, 1);

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), notNullValue());
        assertThat(items.get(0).getUserId(), equalTo(userDto.getId()));
        assertThat(items.get(0).getName(), equalTo(sourceItems.get(1).getName()));
        assertThat(items.get(0).getDescription(), equalTo(sourceItems.get(1).getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(sourceItems.get(1).getAvailable()));
    }

    @Test
    void search() {
        // given & when
        UserDto userDto = makeUserDto("Alexey", "alexey@ya.ru");
        userDto = userService.addUser(userDto);

        List<ItemDto> sourceItems = List.of(
                makeItemDto("Microwave oven", "Power compact microwave oven", true),
                makeItemDto("TV", "Large color TV", true)
        );

        itemService.addItem(userDto.getId(), sourceItems.get(0));
        itemService.addItem(userDto.getId(), sourceItems.get(1));

        List<ItemDto> items = itemService.search("color", 0, 1);

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), notNullValue());
        assertThat(items.get(0).getUserId(), equalTo(userDto.getId()));
        assertThat(items.get(0).getName(), equalTo(sourceItems.get(1).getName()));
        assertThat(items.get(0).getDescription(), equalTo(sourceItems.get(1).getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(sourceItems.get(1).getAvailable()));
    }

    @Test
    void addComment() throws InterruptedException {
        // given & when
        UserDto userDto = userService.addUser(makeUserDto("Alexey", "alexey@ya.ru"));
        UserDto bookerDto = userService.addUser(makeUserDto("Ilya", "ilya@ya.ru"));
        ItemDto itemDto = itemService.addItem(userDto.getId(),
                makeItemDto("Microwave oven", "Power compact microwave oven", true));
        BookingDto bookingDto = new BookingDto();
        bookingDto.setBooker(bookerDto);
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setItem(itemDto);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        String start = dateTime.format(formatter);
        dateTime = dateTime.plusSeconds(1L);
        String end = dateTime.format(formatter);

        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        bookingDto.setStatus("APPROVED");
        bookingDto = bookingService.addBooking(bookerDto.getId(), bookingDto);

        Thread.sleep(1000);

        CommentDto commentDto = itemService.addComment(bookerDto.getId(), itemDto.getId(), makeCommentDto("Cool"));

        // then
        TypedQuery<User> userQuery = em.createQuery("Select us from User us where us.id = :id", User.class);
        User user = userQuery.setParameter("id", bookerDto.getId())
                .getSingleResult();

        TypedQuery<Item> itemQuery = em.createQuery("Select it from Item it where it.id = :id", Item.class);
        Item item = itemQuery.setParameter("id", itemDto.getId())
                .getSingleResult();

        TypedQuery<Comment> query = em.createQuery("Select co from Comment co " +
                "join Item it on co.item = it.id join User us on co.author = us.id where co.id = :id", Comment.class);
        Comment comment = query.setParameter("id", commentDto.getId())
                .getSingleResult();

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getAuthor(), equalTo(user));
        assertThat(comment.getItem(), equalTo(item));
        assertThat(comment.getText(), equalTo(commentDto.getText()));

        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        List<CommentDto> commentDtoList = CommentMapper.mapToCommentsDto(commentList);

        assertThat(commentDtoList.get(0), equalTo(commentDto));
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

    private CommentDto makeCommentDto(String text) {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(text);
        return commentDto;
    }
}