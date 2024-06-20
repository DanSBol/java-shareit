package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.config.PersistenceConfig;
import ru.practicum.shareit.exception.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = { "db.name=test"})
@SpringJUnitConfig({PersistenceConfig.class, UserServiceImpl.class})
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService service;

    @Test
    void addUser() {
        // given
        UserDto userDto = makeUserDto("Пётр", "some@email.com");

        // when
        service.addUser(userDto);

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser() {
        // given
        UserDto originUserDto = makeUserDto("Пётр", "petr@ya.ru");
        UserDto updatedUserDto = makeUserDto("Алексей", "alexey@ya.ru");

        // when
        originUserDto = service.addUser(originUserDto);
        service.updateUser(originUserDto.getId(), updatedUserDto);

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", updatedUserDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(updatedUserDto.getName()));
        assertThat(user.getEmail(), equalTo(updatedUserDto.getEmail()));
    }

    @Test
    void updateUser_404_not_found() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        assertThatThrownBy(() -> service.updateUser(1L, userDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found.");
    }

    @Test
    void updateUser_404_not_found_one_more() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            service.updateUser(1L, userDto);
        }, "User not found.");

        Assertions.assertEquals("User not found.", thrown.getMessage());
    }

    @Test
    void deleteUser() {
        // given
        UserDto userDto = makeUserDto("Пётр", "petr@ya.ru");

        // when
        userDto = service.addUser(userDto);
        service.deleteUser(userDto.getId());

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        List<User> user = query.setParameter("email", userDto.getEmail())
                .getResultList();

        assertThat(String.valueOf(user.isEmpty()), true);
    }

    @Test
    void getUser() {
        // given
        UserDto userDto = makeUserDto("Пётр", "petr@ya.ru");

        // when
        userDto = service.addUser(userDto);
        UserDto getUserDto = service.getUser(userDto.getId());

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(getUserDto.getName()));
        assertThat(user.getEmail(), equalTo(getUserDto.getEmail()));
    }

    @Test
    void getUser_404_not_found() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            service.getUser(1L);
        });

        Assertions.assertEquals("User not found.", thrown.getMessage());
    }

    @Test
    void getAllUsers() {
        // given
        List<UserDto> sourceUsers = List.of(
                makeUserDto("Ivan", "ivan@ya.ru"),
                makeUserDto("Petr", "petr@ya.ru")
        );

        for (UserDto user : sourceUsers) {
            User entity = UserMapper.mapToNewUser(user);
            em.persist(entity);
        }
        em.flush();

        // when
        List<UserDto> targetUsers = service.getAllUsers();

        // then
        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}