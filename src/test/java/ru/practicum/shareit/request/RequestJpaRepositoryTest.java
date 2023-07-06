package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class RequestJpaRepositoryTest {
    private User userOne;
    private User userTwo;
    private ItemRequest itemRequestOne;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

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

        itemRequestOne = new ItemRequest();
        itemRequestOne.setDescription("Дрель помощнее");
        itemRequestOne.setId(1L);
        itemRequestOne.setRequestor(1L);
        itemRequestOne.setCreated(LocalDateTime.now());

        userRepository.save(userOne);
        userRepository.save(userTwo);

        itemRequestRepository.save(itemRequestOne);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findItemRequestsByUserIdTest() {
        List<Optional<ItemRequest>> itemRequests = itemRequestRepository.findItemRequestsByUserId(1L);

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.get(0).get().getDescription(), equalTo("Дрель помощнее"));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findItemRequestsByAnotherUsersTest() {
        List<Optional<ItemRequest>> itemRequests = itemRequestRepository.findItemRequestsByAnotherUsers(2L, 0, 20);

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.get(0).get().getDescription(), equalTo("Дрель помощнее"));
    }


}
