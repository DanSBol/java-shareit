package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.config.PersistenceConfig;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = { "db.name=test"})
@SpringJUnitConfig({PersistenceConfig.class, UserServiceImpl.class, RequestServiceImpl.class})
class RequestServiceImplTest {

    private final EntityManager em;
    private final UserService userService;
    private final RequestService requestService;

    @Test
    void addRequest() {
        // given & when
        UserDto userDto = makeUserDto("Alexey", "alexey@ya.ru");
        userDto = userService.addUser(userDto);
        RequestDto requestDto = makeRequestDto("TV");
        requestDto = requestService.addRequest(userDto.getId(), requestDto);

        // then
        TypedQuery<User> userQuery = em.createQuery("Select us from User us where us.email = :email",
                User.class);
        User requestor = userQuery.setParameter("email", userDto.getEmail())
                .getSingleResult();
        TypedQuery<Request> requestQuery = em.createQuery("Select re from Request re " +
                "join User us on re.requestor = us.id where us.email = :email", Request.class);
        Request request = requestQuery.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(request.getId(), notNullValue());
        assertThat(request.getRequestor(), equalTo(requestor));
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private RequestDto makeRequestDto(String description) {
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription(description);
        return requestDto;
    }
}