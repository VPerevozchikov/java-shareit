package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class ItemJpaRepositoryTest {

    public User userOne;
    public User userTwo;
    public Item itemOne;
    public Item itemTwo;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {

        userOne = new User();
        userOne.setId(1L);
        userOne.setName("Vlad");
        userOne.setEmail("test@yandex.ru");

        userTwo = new User();
        userTwo.setId(2L);
        userTwo.setName("Ivan");
        userTwo.setEmail("ivan@yandex.ru");

        userRepository.save(userOne);
        userRepository.save(userTwo);

        itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setUser(userOne);
        itemOne.setDescription("1500Vt");
        itemOne.setAvailable(true);
        itemOne.setName("Drill");

        itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setUser(userTwo);
        itemTwo.setAvailable(true);
        itemTwo.setDescription("3000Vt");
        itemTwo.setName("Drill_2");

        itemRepository.save(itemOne);
        itemRepository.save(itemTwo);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findItemRequestByUserIdTest() {

        List<Item> items = itemRepository.findItemsByUser(userOne);

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0), equalTo(itemOne));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void searchItemByTextTest() {

        List<Optional<Item>> items = itemRepository.searchItemsByText("RIL");
        assertThat(items.size(), equalTo(2));
    }
}