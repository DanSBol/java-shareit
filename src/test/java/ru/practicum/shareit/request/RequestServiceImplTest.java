package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.config.PersistenceConfig;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

    @Test
    void addRequest_404_user_not_found() {
        RequestDto requestDto = makeRequestDto("TV");
        assertThatThrownBy(() -> requestService.addRequest(1L, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found.");
    }

    @Test
    void getRequest() {
        // given & when
        UserDto userDto = makeUserDto("Alexey", "alexey@ya.ru");
        userDto = userService.addUser(userDto);
        RequestDto requestDto = makeRequestDto("TV");
        requestDto = requestService.addRequest(userDto.getId(), requestDto);
        RequestDto getRequestDto = requestService.getRequest(userDto.getId(), requestDto.getId());

        assertThat(getRequestDto.getId(), equalTo(requestDto.getId()));
        assertThat(getRequestDto.getUserId(), equalTo(requestDto.getUserId()));
        assertThat(getRequestDto.getDescription(), equalTo(requestDto.getDescription()));
    }

    @Test
    void getRequest_404_user_not_found() {
        assertThatThrownBy(() -> requestService.getRequest(1L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found.");
    }

    @Test
    void getRequestsByOwner() {
        // given & when
        UserDto userDto = makeUserDto("Alexey", "alexey@ya.ru");
        userDto = userService.addUser(userDto);

        List<RequestDto> sourceRequestDto = List.of(
                makeRequestDto("TV"),
                makeRequestDto("oven")
        );
        List<RequestDto> requestDto = List.of(
                requestService.addRequest(userDto.getId(), sourceRequestDto.get(0)),
                requestService.addRequest(userDto.getId(), sourceRequestDto.get(1))
        );

        List<RequestDto> getRequestDto = requestService.getRequestsByOwner(userDto.getId());

        assertThat(requestDto, hasSize(getRequestDto.size()));
        assertThat(requestDto.get(0).getId(), equalTo(getRequestDto.get(0).getId()));
        assertThat(requestDto.get(0).getUserId(), equalTo(getRequestDto.get(0).getUserId()));
        assertThat(requestDto.get(0).getDescription(), equalTo(getRequestDto.get(0).getDescription()));
        assertThat(requestDto.get(1).getId(), equalTo(getRequestDto.get(1).getId()));
        assertThat(requestDto.get(1).getUserId(), equalTo(getRequestDto.get(1).getUserId()));
        assertThat(requestDto.get(1).getDescription(), equalTo(getRequestDto.get(1).getDescription()));
    }

    @Test
    void getRequestsByOwner_404_user_not_found() {
        assertThatThrownBy(() -> requestService.getRequestsByOwner(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found.");
    }

    @Test
    void getRequestsByParam() {
        // given & when
        UserDto firstRequestorDto = makeUserDto("Alexey", "alexey@ya.ru");
        firstRequestorDto = userService.addUser(firstRequestorDto);
        UserDto secondRequestorDto = makeUserDto("Ilya", "ilya@ya.ru");
        secondRequestorDto = userService.addUser(secondRequestorDto);

        List<RequestDto> sourceRequestDto = List.of(
                makeRequestDto("TV"),
                makeRequestDto("oven")
        );

        List<RequestDto> requestDto = List.of(
                requestService.addRequest(firstRequestorDto.getId(), sourceRequestDto.get(0)),
                requestService.addRequest(firstRequestorDto.getId(), sourceRequestDto.get(1))
        );

        List<RequestDto> getRequestDto = requestService.getRequestsByParam(secondRequestorDto.getId(), 1, 1);

        assertThat(getRequestDto, hasSize(1));
        assertThat(requestDto.get(1).getId(), equalTo(getRequestDto.get(0).getId()));
        assertThat(requestDto.get(1).getUserId(), equalTo(getRequestDto.get(0).getUserId()));
        assertThat(requestDto.get(1).getDescription(), equalTo(getRequestDto.get(0).getDescription()));
    }

    @Test
    void getRequestsByParam_404_user_not_found() {
        assertThatThrownBy(() -> requestService.getRequestsByParam(1L, 0, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found.");
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