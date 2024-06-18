package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {UserRepository.class, ItemRepository.class}),
        showSql = false,
        properties = {
        "spring.datasource.url=jdbc:h2:./db/testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})

class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User testUser;
    private Item testItem;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setName("testuser");
        testUser.setEmail("testuser@ya.ru");
        testUser = userRepository.save(testUser);
        testItem = new Item();
        testItem.setOwner(testUser);
        testItem.setName("testItemName");
        testItem.setDescription("testItemDescription");
        testItem.setAvailable(true);
        testItem = itemRepository.save(testItem);
    }

    @AfterEach
    public void tearDown() {
        itemRepository.delete(testItem);
        userRepository.delete(testUser);
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void givenItem_whenSaved_thenCanBeFoundByIt() {
        Item savedItem = itemRepository.findById(testItem.getId()).orElse(null);
        assertNotNull(savedItem);
        assertEquals(savedItem.getOwner(), testUser);
        assertEquals(savedItem.getName(), testItem.getName());
        assertEquals(savedItem.getDescription(), testItem.getDescription());
        assertEquals(savedItem.getAvailable(), testItem.getAvailable());
    }

    @Test
    void givenUser_whenUpdated_thenCanBeFoundByIdWithUpdatedData() {
        testItem.setName("updatedItemName");
        testItem.setDescription("updatedItemDescription");

        User newUser = new User();
        newUser.setName("newTestUser");
        newUser.setEmail("newTestUser@ya.ru");
        newUser = userRepository.save(newUser);
        testItem.setOwner(newUser);

        itemRepository.save(testItem);

        Item updatedItem = itemRepository.findById(testItem.getId())
                .orElse(null);

        assertNotNull(updatedItem);
        assertEquals("updatedItemName", updatedItem.getName());
        assertEquals("updatedItemDescription", updatedItem.getDescription());
        assertEquals(newUser, updatedItem.getOwner());
    }
}