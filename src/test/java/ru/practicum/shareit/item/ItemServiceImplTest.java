package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemServiceImplTest {

    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemMapper itemMapper;
    private BookingMapper bookingMapper;
    private UserService userService;
    private BookingService bookingService;
    private ItemService itemService;

    private User userOne;
    private User userTwo;
    private ItemCreationDto itemCreationDtoOne;
    private Item itemOne;
    private Item itemTwo;
    private Booking bookingOne;
    private Booking bookingTwo;

    private Comment commentOne;
    private Comment commentTwo;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        when(bookingRepository.save(any())).thenAnswer(
                invocation -> invocation.getArgument(0));

        itemMapper = new ItemMapperImpl();
        bookingMapper = new BookingMapperImpl();
        userService = mock(UserService.class);
        bookingService = mock(BookingService.class);

        itemService = new ItemService(itemRepository,
                commentRepository,
                itemMapper,
                bookingMapper,
                userService,
                bookingService);

        userOne = new User();
        userOne.setId(1L);
        userOne.setName("Vlad");
        userOne.setEmail("test@yandex.ru");

        userTwo = new User();
        userTwo.setId(2L);
        userTwo.setName("Ivan");
        userTwo.setEmail("ivan@yandex.ru");

        itemCreationDtoOne = new ItemCreationDto();
        itemCreationDtoOne.setUser(userOne);
        itemCreationDtoOne.setDescription("1500Вт");
        itemCreationDtoOne.setAvailable(true);
        itemCreationDtoOne.setName("Дрель");

        itemOne = itemMapper.toItem(itemCreationDtoOne);
        itemOne.setId(1L);

        itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setUser(userTwo);
        itemTwo.setAvailable(true);
        itemTwo.setDescription("3000Вт");
        itemTwo.setName("Дрель_2");

        bookingOne = new Booking();
        bookingOne.setId(1L);
        bookingOne.setStatus(StatusType.APPROVED);
        bookingOne.setItemId(2L);
        bookingOne.setBooker(userOne);
        bookingOne.setStart(LocalDateTime.now().plusHours(10));
        bookingOne.setEnd(LocalDateTime.now().plusHours(100));

        bookingTwo = new Booking();
        bookingTwo.setId(2L);
        bookingTwo.setStatus(StatusType.APPROVED);
        bookingTwo.setItemId(1L);
        bookingTwo.setBooker(userTwo);
        bookingTwo.setStart(LocalDateTime.now().minusHours(50));
        bookingTwo.setEnd(LocalDateTime.now().minusHours(5));

        commentOne = new Comment();
        commentOne.setId(1L);
        commentOne.setAuthor(userOne);
        commentOne.setItemId(2L);
        commentOne.setText("Вещь хорошая");
        commentOne.setCreated(LocalDateTime.now().plusHours(150));

        commentTwo = new Comment();
        commentTwo.setId(2L);
        commentTwo.setAuthor(userTwo);
        commentTwo.setItemId(1L);
        commentTwo.setText("Вещь плохая");
        commentTwo.setCreated(LocalDateTime.now().plusHours(150));

    }

    @Test
    void shouldAddItem() {

        when(itemRepository.findByNameContainingIgnoreCase(any())).thenReturn(itemOne);
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        ItemDto itemDto = itemService.addItem(1L, itemCreationDtoOne);

        Assertions.assertNotNull(itemDto);
        Assertions.assertEquals(itemDto.getId(), 1);
        Assertions.assertEquals(itemDto.getName(), "Дрель");
        Assertions.assertEquals(itemDto.getUser(), userOne);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void shouldNotAddItemWithNotDeclareName() throws ValidationException {
        ItemCreationDto itemCreationDto = new ItemCreationDto();
        itemCreationDto.setUser(userOne);
        itemCreationDto.setDescription("1500Вт");
        itemCreationDto.setAvailable(true);

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        itemService.addItem(1L, itemCreationDto);
                    }
                }
        );

        Assertions.assertEquals("Поле name отсутствует или пусто.", ex.getMessage());
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void shouldNotAddItemWithNotDeclareDescription() throws ValidationException {
        ItemCreationDto itemCreationDto = new ItemCreationDto();
        itemCreationDto.setUser(userOne);
        itemCreationDto.setName("Дрель");
        itemCreationDto.setDescription("1500Вт");

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        itemService.addItem(1L, itemCreationDto);
                    }
                }
        );

        Assertions.assertEquals("Поле available отсутствует.", ex.getMessage());
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void shouldNotAddItemWithNotDeclareAvailable() throws ValidationException {
        ItemCreationDto itemCreationDto = new ItemCreationDto();
        itemCreationDto.setUser(userOne);
        itemCreationDto.setName("Дрель");
        itemCreationDto.setAvailable(true);

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        itemService.addItem(1L, itemCreationDto);
                    }
                }
        );

        Assertions.assertEquals("Поле description отсутствует или пусто.", ex.getMessage());
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void shouldGetItemById() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingTwo);

        List<Comment> comments = new ArrayList<>();
        comments.add(commentTwo);

        when(itemRepository.findById(any())).thenReturn(Optional.of(itemOne));
        when(userService.getUserById(userTwo.getId())).thenReturn(userTwo);
        when(bookingService.getBookingsByItemId(itemOne.getId())).thenReturn(bookings);
        when(commentRepository.findCommentsByItemId(itemOne.getId())).thenReturn(comments);
        ItemDto itemDto = itemService.getItemById(2L, 1L);

        Assertions.assertNotNull(itemDto);
        Assertions.assertEquals(itemDto.getId(), 1);
        Assertions.assertEquals(itemDto.getName(), "Дрель");
        Assertions.assertEquals(itemDto.getUser(), userOne);
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    void shouldNotGetItemByWrongId() throws NotFoundException {
        Optional<Item> optionalEmptyItem = Optional.empty();

        when(itemRepository.findById(any())).thenReturn(optionalEmptyItem);
        when(userService.getUserById(userTwo.getId())).thenReturn(userTwo);

        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class, new Executable() {
                    @Override
                    public void execute() {
                        ItemDto itemDto = itemService.getItemById(2L, 1L);
                    }
                }
        );
        Assertions.assertEquals("Вещь не найдена", ex.getMessage());
    }

    @Test
    void shouldDeleteItemById() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(itemOne));

        itemService.deleteItem(itemOne.getId());

        verify(itemRepository, times(1)).deleteById(any());
    }

    @Test
    void shouldNotDeleteItemByWrongId() throws NotFoundException {

        Optional<Item> optionalEmptyItem = Optional.empty();
        when(itemRepository.findById(any())).thenReturn(optionalEmptyItem);

        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class, new Executable() {
                    @Override
                    public void execute() {
                        itemService.deleteItem(1L);
                    }
                }
        );
        Assertions.assertEquals("Вещь не найдена", ex.getMessage());
    }

    @Test
    void shouldGetItemsByUserId() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingTwo);

        List<Comment> comments = new ArrayList<>();
        comments.add(commentTwo);

        List<Item> items = new ArrayList<>();
        items.add(itemOne);

        when(itemRepository.findItemsByUser(userOne)).thenReturn(items);
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        when(bookingService.getBookingsByItemId(itemTwo.getId())).thenReturn(bookings);
        when(commentRepository.findCommentsByItemId(itemOne.getId())).thenReturn(comments);
        Set<ItemDto> itemsDto = itemService.getItemsByUserId(1L);

        Assertions.assertNotNull(itemsDto);
        Assertions.assertEquals(itemsDto.size(), 1);
        verify(itemRepository, times(1)).findItemsByUser(any());
    }

    @Test
    void shouldUpdateItems() {
        ItemCreationDto itemCreationDtoUpdate = new ItemCreationDto();
        itemCreationDtoUpdate.setUser(userOne);
        itemCreationDtoUpdate.setDescription("1500Вт");
        itemCreationDtoUpdate.setAvailable(true);
        itemCreationDtoUpdate.setName("Супердрель");

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingTwo);

        List<Comment> comments = new ArrayList<>();
        comments.add(commentTwo);

        List<Item> items = new ArrayList<>();
        items.add(itemOne);

        when(itemRepository.findById(any())).thenReturn(Optional.of(itemOne));
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        ItemDto itemDtoUpdate = itemService.updateItem(1L, 1L, itemCreationDtoUpdate);

        Assertions.assertNotNull(itemDtoUpdate);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void shouldNotUpdateItemByWrongId() throws NotFoundException {
        ItemCreationDto itemCreationDtoUpdate = new ItemCreationDto();
        itemCreationDtoUpdate.setUser(userOne);
        itemCreationDtoUpdate.setDescription("1500Вт");
        itemCreationDtoUpdate.setAvailable(true);
        itemCreationDtoUpdate.setName("Супердрель");

        Optional<Item> optionalEmptyItem = Optional.empty();

        when(itemRepository.findById(any())).thenReturn(optionalEmptyItem);
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);

        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class, new Executable() {
                    @Override
                    public void execute() {
                        itemService.updateItem(1L, 1L, itemCreationDtoUpdate);
                    }
                }
        );
        Assertions.assertEquals("Пользователь и/или вещь не найдены.", ex.getMessage());
    }

    @Test
    void shouldSearchItem() {

        List<Item> items = new ArrayList<>();
        items.add(itemOne);

        List<Optional<Item>> itemsOptional = new ArrayList<>();
        items.forEach(item -> itemsOptional.add(Optional.of(item)));

        when(itemRepository.searchItemsByText(any())).thenReturn(itemsOptional);
        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        List<ItemDto> itemsDto = itemService.searchItem(1L, "Дрель");

        Assertions.assertNotNull(itemsDto);
        Assertions.assertEquals(itemsDto.size(), 1);
        verify(itemRepository, times(1)).searchItemsByText(any());
    }

    @Test
    void shouldGetEmptyResultsOfSearchItemByEmptyText() {

        when(userService.getUserById(userOne.getId())).thenReturn(userOne);
        List<ItemDto> itemsDto = itemService.searchItem(1L, " ");

        Assertions.assertNotNull(itemsDto);
        Assertions.assertEquals(itemsDto.size(), 0);
        verify(itemRepository, times(0)).searchItemsByText(any());
    }

    @Test
    void shouldAddComment() {
        CommentCreationDto commentCreationDto = new CommentCreationDto();

        commentCreationDto.setAuthor(userTwo);
        commentCreationDto.setItemId(1L);
        commentCreationDto.setText("Вещь плохая");

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingTwo);

        List<BookingDto> bookingsDto = new ArrayList<>();
        bookings.forEach(booking -> bookingsDto.add(bookingMapper.toDto(booking, itemOne)));

        when(itemRepository.findById(any())).thenReturn(Optional.of(itemOne));
        when(bookingService.getBookingsByBookerId(any(), any(), any(), any())).thenReturn(bookingsDto);
        when(userService.getUserById(userTwo.getId())).thenReturn(userTwo);
        when(commentRepository.findById(any())).thenReturn(Optional.of(commentTwo));
        CommentDto commentDto = itemService.addComment(2L, 1L, commentCreationDto);

        Assertions.assertNotNull(commentDto);
        Assertions.assertEquals(commentDto.getText(), "Вещь плохая");
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void shouldNotAddCommentWithEmptyText() throws ValidationException {
        CommentCreationDto commentCreationDto = new CommentCreationDto();

        commentCreationDto.setAuthor(userTwo);
        commentCreationDto.setItemId(1L);
        commentCreationDto.setText(" ");


        when(itemRepository.findById(any())).thenReturn(Optional.of(itemOne));
        when(userService.getUserById(userTwo.getId())).thenReturn(userTwo);
        when(commentRepository.findById(any())).thenReturn(Optional.of(commentTwo));

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        itemService.addComment(2L, 1L, commentCreationDto);
                    }
                }
        );

        Assertions.assertEquals("Поле text отсутствует или пусто.", ex.getMessage());
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    void shouldNotAddCommentWithWrongItemId() throws NotFoundException {
        CommentCreationDto commentCreationDto = new CommentCreationDto();
        commentCreationDto.setAuthor(userTwo);
        commentCreationDto.setItemId(1L);
        commentCreationDto.setText("Вещь хорошая");

        Optional<Item> optionalEmptyItem = Optional.empty();

        when(itemRepository.findById(any())).thenReturn(optionalEmptyItem);
        when(userService.getUserById(userTwo.getId())).thenReturn(userTwo);

        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class, new Executable() {
                    @Override
                    public void execute() {
                        itemService.addComment(2L, 1L, commentCreationDto);
                    }
                }
        );
        Assertions.assertEquals("Вещь не найдена.", ex.getMessage());
    }

    @Test
    void shouldNotAddCommentWithBadRequest() throws ValidationException {
        CommentCreationDto commentCreationDto = new CommentCreationDto();

        commentCreationDto.setAuthor(userTwo);
        commentCreationDto.setItemId(1L);
        commentCreationDto.setText("Вещь хорошая");

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingTwo);

        List<BookingDto> bookingsDto = new ArrayList<>();
        bookings.forEach(booking -> bookingsDto.add(bookingMapper.toDto(booking, itemOne)));

        when(itemRepository.findById(any())).thenReturn(Optional.of(itemOne));
        when(bookingService.getBookingsByBookerId(1L, "PAST", 0, 100)).thenReturn(bookingsDto);
        when(userService.getUserById(userTwo.getId())).thenReturn(userTwo);
        when(commentRepository.findById(commentTwo.getId())).thenReturn(Optional.of(commentTwo));

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        itemService.addComment(2L, 1L, commentCreationDto);
                    }
                }
        );

        Assertions.assertEquals("Пользователь находится в процессе использования вещи и не может оставить комментарий." +
                "Или пользователь не бронировал вещь и не может оставить комментарий.", ex.getMessage());
        verify(itemRepository, times(0)).save(any());
    }
}
