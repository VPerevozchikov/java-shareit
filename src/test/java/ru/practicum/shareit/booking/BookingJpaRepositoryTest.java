package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class BookingJpaRepositoryTest {
    private User userOne;
    private User userTwo;
    private Item itemOne;
    private Item itemTwo;
    private Booking bookingOne;
    private Booking bookingTwo;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

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

        bookingOne = new Booking();
        bookingOne.setId(1L);
        bookingOne.setStatus(StatusType.APPROVED);
        bookingOne.setItemId(2L);
        bookingOne.setBooker(userOne);
        bookingOne.setStart(LocalDateTime.now().plusHours(10));
        bookingOne.setEnd(LocalDateTime.now().plusHours(100));

        bookingTwo = new Booking();
        bookingOne.setId(2L);
        bookingTwo.setStatus(StatusType.WAITING);
        bookingTwo.setItemId(1L);
        bookingTwo.setBooker(userTwo);
        bookingTwo.setStart(LocalDateTime.now().plusHours(10));
        bookingTwo.setEnd(LocalDateTime.now().plusHours(100));

        userRepository.save(userOne);
        userRepository.save(userTwo);

        itemRepository.save(itemOne);
        itemRepository.save(itemTwo);

        bookingRepository.save(bookingOne);
        bookingRepository.save(bookingTwo);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findBookersByBookerIdAndTypicalStatusTest() {
        List<Optional<Booking>> bookings = bookingRepository.findBookersByBookerIdAndTypicalStatus(2L, "WAITING");

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).get().getItemId(), equalTo(1L));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findBookersByBookerIdTest() {
        PageRequest page = PageRequest.of(0, 5);

        Page<Booking> bookings = bookingRepository.findBookersByBookerId(1L, page);
        assertThat(bookings.getNumberOfElements(), equalTo(1));
        assertThat(bookings.getContent().get(0).getItemId(), equalTo(2L));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findFutureBookersByBookerIdTest() {
        List<Optional<Booking>> bookings = bookingRepository.findFutureBookersByBookerId(2L, LocalDateTime.now());

        assertThat(bookings.size(), equalTo(1));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findPastBookersByBookerIdTest() {
        List<Optional<Booking>> bookings = bookingRepository.findPastBookersByBookerId(2L, LocalDateTime.now());

        assertThat(bookings.size(), equalTo(0));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findCurrentBookersByBookerIdTest() {
        List<Optional<Booking>> bookings = bookingRepository.findCurrentBookersByBookerId(2L, LocalDateTime.now());

        assertThat(bookings.size(), equalTo(0));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findBookersByOwnerIdTest() {
        PageRequest page = PageRequest.of(0, 5);

        Page<Booking> bookings = bookingRepository.findBookingsByOwnerId(1L, page);
        assertThat(bookings.getNumberOfElements(), equalTo(2));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findFutureBookersByOwnerIdTest() {
        List<Optional<Booking>> bookings = bookingRepository.findFutureBookersByOwnerId(2L, LocalDateTime.now());

        assertThat(bookings.size(), equalTo(1));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findBookersByOwnerIdAndTypicalStatusTest() {
        List<Optional<Booking>> bookings = bookingRepository.findBookersByOwnerIdAndTypicalStatus(2L, "APPROVED");

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).get().getItemId(), equalTo(2L));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findPastBookersByOwnerIdTest() {
        List<Optional<Booking>> bookings = bookingRepository.findPastBookersByOwnerId(2L, LocalDateTime.now());

        assertThat(bookings.size(), equalTo(0));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findCurrentBookersByOwnerIdTest() {
        List<Optional<Booking>> bookings = bookingRepository.findCurrentBookersByOwnerId(2L, LocalDateTime.now());

        assertThat(bookings.size(), equalTo(0));
    }

}
