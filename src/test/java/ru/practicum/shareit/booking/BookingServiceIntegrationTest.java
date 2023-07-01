package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest()
public class BookingServiceIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    public UserCreationDto userCreationDtoOne;
    public UserCreationDto userCreationDtoTwo;
    private ItemCreationDto itemCreationDtoOne;
    private ItemCreationDto itemCreationDtoTwo;
    private ItemCreationDto itemCreationDtoThree;
    private BookingCreationDto bookingCreationDto;
    private CommentCreationDto commentCreationDto;

    @BeforeEach
    void setUp() {
        userCreationDtoOne = new UserCreationDto();
        userCreationDtoOne.setName("Vlad");
        userCreationDtoOne.setEmail("test@yandex.ru");

        userCreationDtoTwo = new UserCreationDto();
        userCreationDtoTwo.setName("Ivan");
        userCreationDtoTwo.setEmail("ivan@yandex.ru");

        itemCreationDtoOne = new ItemCreationDto();
        itemCreationDtoOne.setDescription("1500Вт");
        itemCreationDtoOne.setAvailable(true);
        itemCreationDtoOne.setName("Дрель");

        itemCreationDtoTwo = new ItemCreationDto();
        itemCreationDtoTwo.setDescription("3000Вт");
        itemCreationDtoTwo.setAvailable(true);
        itemCreationDtoTwo.setName("Дрель_2");

        itemCreationDtoThree = new ItemCreationDto();
        itemCreationDtoThree.setDescription("4000Вт");
        itemCreationDtoThree.setAvailable(true);
        itemCreationDtoThree.setName("Дрель_3");

        bookingCreationDto = new BookingCreationDto();
        bookingCreationDto.setStatus(StatusType.APPROVED);
        bookingCreationDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingCreationDto.setEnd(LocalDateTime.now().plusSeconds(2));

        commentCreationDto = new CommentCreationDto();
        commentCreationDto.setText("Good thing");
    }

    @Test
    @SneakyThrows
    void getBookingsByBookerIdTest() {
        UserDto userDtoOne = userService.addUser(userCreationDtoOne);
        UserDto userDtoTwo = userService.addUser(userCreationDtoTwo);

        User userOne = new User();
        userOne.setId(userDtoOne.getId());
        userOne.setName(userDtoOne.getName());
        userOne.setEmail(userDtoOne.getEmail());

        User userTwo = new User();
        userTwo.setId(userDtoTwo.getId());
        userTwo.setName(userDtoTwo.getName());
        userTwo.setEmail(userDtoTwo.getEmail());

        ItemDto itemDtoOne = itemService.addItem(userOne.getId(), itemCreationDtoOne);
        itemService.addItem(userTwo.getId(), itemCreationDtoTwo);
        itemService.addItem(userOne.getId(), itemCreationDtoThree);

        bookingCreationDto.setItemId(itemDtoOne.getId());

        BookingDto booking = bookingService.addBooking(userTwo.getId(), bookingCreationDto);
        TimeUnit.SECONDS.sleep(3);
        CommentDto comment = itemService.addComment(userTwo.getId(), itemDtoOne.getId(), commentCreationDto);

        List<BookingDto> bookingsDtoList = bookingService.getBookingsByBookerId(userTwo.getId(),
                "ALL", 0, 20);

        assertThat(bookingsDtoList.size(), equalTo(1));
        assertThat(bookingsDtoList.get(0).getId(), equalTo(booking.getId()));
        assertThat(bookingsDtoList.get(0).getItem().getName(), equalTo(itemDtoOne.getName()));
    }
}
