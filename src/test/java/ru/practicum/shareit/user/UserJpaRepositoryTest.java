package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class UserJpaRepositoryTest {
    private User userOne;
    private User userTwo;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
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
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findUserByEmailTest() {
        User user = userRepository.findByEmailContainingIgnoreCase("test@yandex.ru");

        assertThat(user, equalTo(userOne));
    }
}

