package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationBookingStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookingServiceImplTest {

    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private User userOne;
    private User userTwo;
    private Item itemOne;
    private Item itemTwo;
    private BookingCreationDto bookingCreationDtoOne;
    private BookingCreationDto bookingCreationDtoWrong;
    private Booking bookingOne;
    private Booking bookingPast;
    private Booking bookingRejected;
    private Booking bookingFuture;
    private Booking bookingCurrent;
    private List<Booking> listOfAllBookings;
    private Booking bookingWrong;
    private ItemMapper itemMapper;
    private BookingMapper bookingMapper;
    private UserService userService;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        when(bookingRepository.save(any())).thenAnswer(
                invocation -> invocation.getArgument(0));
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        itemMapper = new ItemMapperImpl();
        bookingMapper = new BookingMapperImpl();
        userService = mock(UserService.class);
        bookingService = new BookingServiceImpl(itemRepository,
                itemMapper,
                bookingRepository,
                bookingMapper,
                userService);
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
        itemOne.setAvailable(true);
        itemOne.setDescription("1500Вт");
        itemOne.setName("Дрель_1");

        itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setUser(userTwo);
        itemTwo.setAvailable(true);
        itemTwo.setDescription("3000Вт");
        itemTwo.setName("Дрель_2");

        bookingCreationDtoOne = new BookingCreationDto();
        bookingCreationDtoOne.setStatus(StatusType.WAITING);
        bookingCreationDtoOne.setItemId(2L);
        bookingCreationDtoOne.setBooker(userOne);
        bookingCreationDtoOne.setStart(LocalDateTime.now().plusHours(10));
        bookingCreationDtoOne.setEnd(LocalDateTime.now().plusHours(100));

        bookingCreationDtoWrong = new BookingCreationDto();
        bookingCreationDtoWrong.setStatus(StatusType.WAITING);
        bookingCreationDtoWrong.setItemId(1L);
        bookingCreationDtoWrong.setBooker(userOne);
        bookingCreationDtoWrong.setStart(LocalDateTime.now().plusHours(10));
        bookingCreationDtoWrong.setEnd(LocalDateTime.now().plusHours(100));

        bookingOne = bookingMapper.toBooking(bookingCreationDtoOne);
        bookingOne.setId(1L);

        bookingWrong = bookingMapper.toBooking(bookingCreationDtoWrong);
        bookingWrong.setId(1L);

        bookingPast = new Booking();
        bookingPast.setStatus(StatusType.APPROVED);
        bookingPast.setItemId(2L);
        bookingPast.setBooker(userOne);
        bookingPast.setStart(LocalDateTime.now().minusHours(100));
        bookingPast.setEnd(LocalDateTime.now().minusHours(10));

        bookingRejected = new Booking();
        bookingRejected.setStatus(StatusType.REJECTED);
        bookingRejected.setItemId(2L);
        bookingRejected.setBooker(userOne);
        bookingRejected.setStart(LocalDateTime.now().plusHours(10));
        bookingRejected.setEnd(LocalDateTime.now().plusHours(100));

        bookingFuture = new Booking();
        bookingFuture.setStatus(StatusType.WAITING);
        bookingFuture.setItemId(2L);
        bookingFuture.setBooker(userOne);
        bookingFuture.setStart(LocalDateTime.now().plusHours(10));
        bookingFuture.setEnd(LocalDateTime.now().plusHours(100));

        bookingCurrent = new Booking();
        bookingCurrent.setStatus(StatusType.APPROVED);
        bookingCurrent.setItemId(2L);
        bookingCurrent.setBooker(userOne);
        bookingCurrent.setStart(LocalDateTime.now().minusHours(10));
        bookingCurrent.setEnd(LocalDateTime.now().plusHours(100));

        listOfAllBookings = new ArrayList<>();
        listOfAllBookings.add(bookingOne);
        listOfAllBookings.add(bookingPast);
        listOfAllBookings.add(bookingRejected);
        listOfAllBookings.add(bookingFuture);
        listOfAllBookings.add(bookingCurrent);
    }

    @Test
    void shouldAddBooking() {

        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findById(bookingOne.getId())).thenReturn(Optional.of(bookingOne));
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        var result = bookingService.addBooking(1L, bookingCreationDtoOne);

        Assertions.assertNotNull(result);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void shouldNotAddBookingWithItemIdSameUserId() throws NotFoundException {

        when(itemRepository.findById(itemOne.getId())).thenReturn(Optional.of(itemOne));
        when(bookingRepository.findById(bookingWrong.getId())).thenReturn(Optional.of(bookingWrong));
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class, new Executable() {
                    @Override
                    public void execute() {
                        bookingService.addBooking(1L, bookingCreationDtoWrong);
                    }
                }
        );

        Assertions.assertEquals("Попытка создань бронь владельца вещи на собственную вещь", ex.getMessage());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void shouldNotAddBookingWithNotDeclareTime() throws ValidationException {

        BookingCreationDto bookingCreationDtoWithNotDeclareTime = new BookingCreationDto();
        bookingCreationDtoWithNotDeclareTime.setStatus(StatusType.WAITING);
        bookingCreationDtoWithNotDeclareTime.setItemId(1L);
        bookingCreationDtoWithNotDeclareTime.setBooker(userOne);

        when(itemRepository.findById(itemOne.getId())).thenReturn(Optional.of(itemOne));
        when(bookingRepository.findById(bookingWrong.getId())).thenReturn(Optional.of(bookingWrong));
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        bookingService.addBooking(1L, bookingCreationDtoWithNotDeclareTime);
                    }
                }
        );

        Assertions.assertEquals("Время старта и/или окончания бронирования не задано", ex.getMessage());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void shouldNotAddBookingWithPastTime() throws ValidationException {

        BookingCreationDto bookingCreationDtoWithPastTime = new BookingCreationDto();
        bookingCreationDtoWithPastTime.setStatus(StatusType.WAITING);
        bookingCreationDtoWithPastTime.setItemId(1L);
        bookingCreationDtoWithPastTime.setBooker(userOne);
        bookingCreationDtoWithPastTime.setStart(LocalDateTime.now().minusHours(100));
        bookingCreationDtoWithPastTime.setEnd(LocalDateTime.now().minusHours(10));

        when(itemRepository.findById(itemOne.getId())).thenReturn(Optional.of(itemOne));
        when(bookingRepository.findById(bookingWrong.getId())).thenReturn(Optional.of(bookingWrong));
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        bookingService.addBooking(1L, bookingCreationDtoWithPastTime);
                    }
                }
        );

        Assertions.assertEquals("Время окончания и/или старта в прошлом", ex.getMessage());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void shouldNotAddBookingWithEndTimeBeforeStartTime() throws ValidationException {

        BookingCreationDto bookingCreationDtoWithEndTimeBeforeStartTime = new BookingCreationDto();
        bookingCreationDtoWithEndTimeBeforeStartTime.setStatus(StatusType.WAITING);
        bookingCreationDtoWithEndTimeBeforeStartTime.setItemId(1L);
        bookingCreationDtoWithEndTimeBeforeStartTime.setBooker(userOne);
        bookingCreationDtoWithEndTimeBeforeStartTime.setStart(LocalDateTime.now().plusHours(100));
        bookingCreationDtoWithEndTimeBeforeStartTime.setEnd(LocalDateTime.now().plusHours(10));

        when(itemRepository.findById(itemOne.getId())).thenReturn(Optional.of(itemOne));
        when(bookingRepository.findById(bookingWrong.getId())).thenReturn(Optional.of(bookingWrong));
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        bookingService.addBooking(1L, bookingCreationDtoWithEndTimeBeforeStartTime);
                    }
                }
        );

        Assertions.assertEquals("Значение даты окончания задано как более раннее в сравнение с датой старта", ex.getMessage());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void shouldNotAddBookingWithEndTimeSameStartTime() throws ValidationException {

        BookingCreationDto bookingCreationDtoWithEndTimeBeforeStartTime = new BookingCreationDto();
        bookingCreationDtoWithEndTimeBeforeStartTime.setStatus(StatusType.WAITING);
        bookingCreationDtoWithEndTimeBeforeStartTime.setItemId(1L);
        bookingCreationDtoWithEndTimeBeforeStartTime.setBooker(userOne);
        bookingCreationDtoWithEndTimeBeforeStartTime.setStart(LocalDateTime.of(2023, 10, 05, 05, 05));
        bookingCreationDtoWithEndTimeBeforeStartTime.setEnd(LocalDateTime.of(2023, 10, 05, 05, 05));

        when(itemRepository.findById(itemOne.getId())).thenReturn(Optional.of(itemOne));
        when(bookingRepository.findById(bookingWrong.getId())).thenReturn(Optional.of(bookingWrong));
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        bookingService.addBooking(1L, bookingCreationDtoWithEndTimeBeforeStartTime);
                    }
                }
        );

        Assertions.assertEquals("Значение даты окончания равно дате старта", ex.getMessage());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void shouldApproveBooking() {

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findById(bookingOne.getId())).thenReturn(Optional.of(bookingOne));
        var result = bookingService.approveBooking(2L, 1L, "true");

        Assertions.assertNotNull(result);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void shouldNotApproveBooking() throws ValidationException {

        Booking bookingAlsoApproved = new Booking();
        bookingAlsoApproved.setId(1L);
        bookingAlsoApproved.setItemId(itemOne.getId());
        bookingAlsoApproved.setStatus(StatusType.APPROVED);
        bookingAlsoApproved.setBooker(userTwo);
        bookingAlsoApproved.setStart(LocalDateTime.now().plusHours(10));
        bookingAlsoApproved.setEnd(LocalDateTime.now().plusHours(100));

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemOne.getId())).thenReturn(Optional.of(itemOne));
        when(bookingRepository.findById(bookingAlsoApproved.getId())).thenReturn(Optional.of(bookingAlsoApproved));
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        bookingService.approveBooking(1L, 1L, "true");
                    }
                }
        );

        Assertions.assertEquals("Попытка повторно одобрить бронь.", ex.getMessage());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void shouldGetBookingById() {

        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findById(bookingOne.getId())).thenReturn(Optional.of(bookingOne));
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        var result = bookingService.getBookingById(1L, 1L);

        Assertions.assertNotNull(result);
        verify(bookingRepository, times(2)).findById(any());
    }

    @Test
    void shouldNotGetBookingByWrongId() throws NotFoundException {

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class, new Executable() {
                    @Override
                    public void execute() {
                        bookingService.getBookingById(1L, 2L);
                    }
                }
        );

        Assertions.assertEquals("Бронь не найдена.", ex.getMessage());
    }

    @Test
    void shouldGetBookingByBookerId() {
        //ALL
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findBookersByBookerId(any(), any())).thenReturn(new PageImpl<>(listOfAllBookings));
        List<BookingDto> bookings = bookingService.getBookingsByBookerId(1L, "ALL", 0, 2);

        assertEquals(5, bookings.size());
        verify(bookingRepository, times(1)).findBookersByBookerId(any(), any());

        //WAITING
        List<Optional<Booking>> listOfWaitingBookings = new ArrayList<>();
        listOfWaitingBookings.add(Optional.of(bookingFuture));

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findBookersByBookerIdAndTypicalStatus(any(), any())).thenReturn((listOfWaitingBookings));
        List<BookingDto> bookingsWaiting = bookingService.getBookingsByBookerId(1L, "WAITING", 0, 2);

        assertEquals(1, bookingsWaiting.size());
        verify(bookingRepository, times(1)).findBookersByBookerIdAndTypicalStatus(any(), any());

        //REJECTED
        List<Optional<Booking>> listOfRejectedBookings = new ArrayList<>();
        listOfRejectedBookings.add(Optional.of(bookingRejected));

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findBookersByBookerIdAndTypicalStatus(any(), any())).thenReturn((listOfRejectedBookings));
        List<BookingDto> bookingsRejected = bookingService.getBookingsByBookerId(1L, "REJECTED", 0, 2);

        assertEquals(1, bookingsRejected.size());
        verify(bookingRepository, times(2)).findBookersByBookerIdAndTypicalStatus(any(), any());

        //FUTURE
        List<Optional<Booking>> listOfFutureBookings = new ArrayList<>();
        listOfFutureBookings.add(Optional.of(bookingFuture));

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findFutureBookersByBookerId(any(), any())).thenReturn((listOfFutureBookings));
        List<BookingDto> bookingsFuture = bookingService.getBookingsByBookerId(1L, "FUTURE", 0, 2);

        assertEquals(1, bookingsFuture.size());
        verify(bookingRepository, times(1)).findFutureBookersByBookerId(any(), any());

        //PAST
        List<Optional<Booking>> listOfPastBookings = new ArrayList<>();
        listOfPastBookings.add(Optional.of(bookingPast));

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findPastBookersByBookerId(any(), any())).thenReturn((listOfPastBookings));
        List<BookingDto> bookingsPast = bookingService.getBookingsByBookerId(1L, "PAST", 0, 2);

        assertEquals(1, bookingsPast.size());
        verify(bookingRepository, times(1)).findPastBookersByBookerId(any(), any());

        //CURRENT
        List<Optional<Booking>> listOfCurrentBookings = new ArrayList<>();
        listOfCurrentBookings.add(Optional.of(bookingPast));

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findCurrentBookersByBookerId(any(), any())).thenReturn((listOfCurrentBookings));
        List<BookingDto> bookingsCurrent = bookingService.getBookingsByBookerId(1L, "CURRENT", 0, 2);

        assertEquals(1, bookingsCurrent.size());
        verify(bookingRepository, times(1)).findCurrentBookersByBookerId(any(), any());
    }

    @Test
    void shouldGetBookingByOwnerId() {
        //ALL
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findBookingsByOwnerId(any(), any())).thenReturn(new PageImpl<>(listOfAllBookings));
        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(1L, "ALL", 0, 2);

        assertEquals(5, bookings.size());
        verify(bookingRepository, times(1)).findBookingsByOwnerId(any(), any());

        //WAITING
        List<Optional<Booking>> listOfWaitingBookings = new ArrayList<>();
        listOfWaitingBookings.add(Optional.of(bookingFuture));

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findBookersByOwnerIdAndTypicalStatus(any(), any())).thenReturn((listOfWaitingBookings));
        List<BookingDto> bookingsWaiting = bookingService.getBookingsByOwnerId(1L, "WAITING", 0, 2);

        assertEquals(1, bookingsWaiting.size());
        verify(bookingRepository, times(1)).findBookersByOwnerIdAndTypicalStatus(any(), any());

        //REJECTED
        List<Optional<Booking>> listOfRejectedBookings = new ArrayList<>();
        listOfRejectedBookings.add(Optional.of(bookingRejected));

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findBookersByOwnerIdAndTypicalStatus(any(), any())).thenReturn((listOfRejectedBookings));
        List<BookingDto> bookingsRejected = bookingService.getBookingsByOwnerId(1L, "REJECTED", 0, 2);

        assertEquals(1, bookingsRejected.size());
        verify(bookingRepository, times(2)).findBookersByOwnerIdAndTypicalStatus(any(), any());

        //FUTURE
        List<Optional<Booking>> listOfFutureBookings = new ArrayList<>();
        listOfFutureBookings.add(Optional.of(bookingFuture));

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findFutureBookersByOwnerId(any(), any())).thenReturn((listOfFutureBookings));
        List<BookingDto> bookingsFuture = bookingService.getBookingsByOwnerId(1L, "FUTURE", 0, 2);

        assertEquals(1, bookingsFuture.size());
        verify(bookingRepository, times(1)).findFutureBookersByOwnerId(any(), any());

        //PAST
        List<Optional<Booking>> listOfPastBookings = new ArrayList<>();
        listOfPastBookings.add(Optional.of(bookingPast));

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findPastBookersByOwnerId(any(), any())).thenReturn((listOfPastBookings));
        List<BookingDto> bookingsPast = bookingService.getBookingsByOwnerId(1L, "PAST", 0, 2);

        assertEquals(1, bookingsPast.size());
        verify(bookingRepository, times(1)).findPastBookersByOwnerId(any(), any());

        //CURRENT
        List<Optional<Booking>> listOfCurrentBookings = new ArrayList<>();
        listOfCurrentBookings.add(Optional.of(bookingPast));

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findCurrentBookersByOwnerId(any(), any())).thenReturn((listOfCurrentBookings));
        List<BookingDto> bookingsCurrent = bookingService.getBookingsByOwnerId(1L, "CURRENT", 0, 2);

        assertEquals(1, bookingsCurrent.size());
        verify(bookingRepository, times(1)).findCurrentBookersByOwnerId(any(), any());

    }

    @Test
    void shouldNotGetBookingByBookerOrOwnerIdWithWrongState() throws ValidationBookingStatusException {

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findBookersByBookerId(any(), any())).thenReturn(new PageImpl<>(listOfAllBookings));
        ValidationBookingStatusException ex = Assertions.assertThrows(
                ValidationBookingStatusException.class, new Executable() {
                    @Override
                    public void execute() {
                        bookingService.getBookingsByBookerId(1L, "ALLL", 0, 2);
                    }
                }
        );

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", ex.getMessage());
    }

    @Test
    void shouldNotGetBookingByBookerOrOwnerIdWithWrongPageQuerySize() throws ValidationException {

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findBookersByBookerId(any(), any())).thenReturn(new PageImpl<>(listOfAllBookings));
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        bookingService.getBookingsByBookerId(1L, "ALL", 0, 0);
                    }
                }
        );

        Assertions.assertEquals("Отрицательный или ноль параметр запроса size", ex.getMessage());
    }

    @Test
    void shouldNotGetBookingByBookerOrOwnerIdWithWrongPageQueryFrom() throws ValidationException {

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(itemRepository.findById(itemTwo.getId())).thenReturn(Optional.of(itemTwo));
        when(bookingRepository.findBookersByBookerId(any(), any())).thenReturn(new PageImpl<>(listOfAllBookings));
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        bookingService.getBookingsByBookerId(1L, "ALL", -1, 2);
                    }
                }
        );

        Assertions.assertEquals("Отрицательный параметр запроса from", ex.getMessage());
    }

    @Test
    void shouldGetBookingByItemId() {
        List<Optional<Booking>> bookings = new ArrayList<>();

        listOfAllBookings.forEach((booking) -> bookings.add(Optional.of(booking)));
        when(bookingRepository.findBookingByItemIdOrderByStartDesc(any())).thenReturn(bookings);
        List<Booking> bookingsByItemId = bookingService.getBookingsByItemId(1L);

        assertEquals(5, bookingsByItemId.size());
        verify(bookingRepository, times(1)).findBookingByItemIdOrderByStartDesc(any());

    }

}


