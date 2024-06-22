package ru.practicum.shareit.request;

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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {UserRepository.class, RequestRepository.class}),
        showSql = false,
        properties = {
        "spring.datasource.url=jdbc:h2:./db/testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})

class RequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private Request request;
    private User owner;
    private User requester;

    @BeforeEach
    public void setUp() {
        owner = new User();
        owner.setName("testOwnerName");
        owner.setEmail("testOwnerEmail@ya.ru");
        owner = userRepository.save(owner);
        requester = new User();
        requester.setName("testRequesterName");
        requester.setEmail("testRequesterEmail@ya.ru");
        requester = userRepository.save(requester);
        request = new Request();
        request.setRequestor(requester);
        request.setDescription("testItemDescription");
        request = requestRepository.save(request);
    }

    @AfterEach
    public void tearDown() {
        requestRepository.delete(request);
        userRepository.delete(requester);
        userRepository.delete(owner);
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void givenRequest_whenSaved_thenCanBeFoundByIt() {
        Request savedRequest = requestRepository.findById(request.getId()).orElse(null);
        assertNotNull(savedRequest);
        assertEquals(savedRequest.getRequestor(), requester);
        assertEquals(savedRequest.getDescription(), request.getDescription());
    }

    @Test
    void givenRequest_whenUpdated_thenCanBeFoundByIdWithUpdatedData() {
        User newRequester = new User();
        newRequester.setName("newTestRequesterName");
        newRequester.setEmail("newTestRequesterEmail@ya.ru");
        newRequester = userRepository.save(newRequester);

        Request newRequest = new Request();
        newRequest.setRequestor(newRequester);
        newRequest.setDescription("anotherItemDescription");
        newRequest = requestRepository.save(newRequest);

        Request updatedRequest = requestRepository.findById(newRequest.getId())
                .orElse(null);

        assertNotNull(updatedRequest);
        assertEquals(newRequester, updatedRequest.getRequestor());
        assertEquals("anotherItemDescription", updatedRequest.getDescription());
        assertEquals(newRequest, updatedRequest);
    }

    @Test
    void getRequestsByOwner() {
        User newRequester = new User();
        newRequester.setName("newTestRequesterName");
        newRequester.setEmail("newTestRequesterEmail@ya.ru");
        newRequester = userRepository.save(newRequester);

        Request newRequest = new Request();
        newRequest.setRequestor(newRequester);
        newRequest.setDescription("anotherItemDescription");
        newRequest = requestRepository.save(newRequest);

        List<Request> requests = requestRepository.getRequestsByOwner(newRequester.getId());
        assertEquals(1, requests.size());
        assertEquals(newRequest.getId(), requests.get(0).getId());
        assertEquals(newRequest.getDescription(), requests.get(0).getDescription());
        assertEquals(newRequest.getRequestor(), requests.get(0).getRequestor());
    }

    @Test
    void getRequestsByParam() {
        User newRequester = new User();
        newRequester.setName("newTestRequesterName");
        newRequester.setEmail("newTestRequesterEmail@ya.ru");
        newRequester = userRepository.save(newRequester);

        Request newRequest = new Request();
        newRequest.setRequestor(newRequester);
        newRequest.setDescription("anotherItemDescription");
        newRequest = requestRepository.save(newRequest);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Request> requests = requestRepository.getRequestsByParam(requester.getId(), pageable);
        assertEquals(1, requests.getSize());
        assertEquals(newRequest.getId(), requests.getContent().get(0).getId());
        assertEquals(newRequest.getDescription(), requests.getContent().get(0).getDescription());
        assertEquals(newRequest.getRequestor(), requests.getContent().get(0).getRequestor());
    }
}