package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RequestServiceImplTest {
    private ItemRepository itemRepository;
    private ItemRequestRepository itemRequestRepository;
    private ItemMapper itemMapper;
    private ItemRequestMapper itemRequestMapper;
    private ItemRequestService itemRequestService;
    private UserService userService;
    private ItemRequestCreationDto itemRequestCreationDtoOne;
    private ItemRequest itemRequestOne;
    private User userOne;
    private User userTwo;
    private Item itemOne;
    private Item itemTwo;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemMapper = new ItemMapperImpl();
        itemRequestMapper = new ItemRequestMapperImpl();
        userService = mock(UserService.class);
        itemRequestService = new ItemRequestServiceImpl(
                itemRepository,
                itemRequestRepository,
                itemMapper,
                itemRequestMapper,
                userService
        );

        itemRequestCreationDtoOne = new ItemRequestCreationDto();
        itemRequestCreationDtoOne.setDescription("Дрель помощнее");

        itemRequestOne = new ItemRequest();
        itemRequestOne.setId(1L);
        itemRequestOne.setRequestor(1L);
        itemRequestOne.setCreated(LocalDateTime.now());
        itemRequestOne.setDescription(itemRequestCreationDtoOne.getDescription());

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
        itemOne.setDescription("1500Вт");
        itemOne.setAvailable(true);
        itemOne.setName("Дрель");
        itemOne.setRequestId(1L);

        itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setUser(userTwo);
        itemTwo.setAvailable(true);
        itemTwo.setDescription("3000Вт");
        itemTwo.setName("Дрель_2");
        itemTwo.setRequestId(1L);
    }

    @Test
    void shouldAddItemRequest() {
        List<Item> items = new ArrayList<>();
        items.add(itemOne);

        when(userService.getUserById(any())).thenReturn(userOne);
        when(itemRepository.findItemsByRequestId(1L)).thenReturn(items);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequestOne));
        ItemRequestDto itemRequestDto = itemRequestService.addItemRequest(1L, itemRequestCreationDtoOne);

        Assertions.assertNotNull(itemRequestDto);
        Assertions.assertEquals(itemRequestDto.getId(), 1L);
        Assertions.assertEquals("Дрель помощнее", itemRequestDto.getDescription());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void shouldNotAddItemRequestWithEmptyDescription() throws ValidationException {
        ItemRequestCreationDto itemRequestCreationDtoWrong = new ItemRequestCreationDto();
        itemRequestCreationDtoOne.setDescription("  ");

        when(userService.getUserById(any())).thenReturn(userOne);

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        itemRequestService.addItemRequest(1L, itemRequestCreationDtoWrong);
                    }
                }
        );

        Assertions.assertEquals("Не задано описание вещи.", ex.getMessage());
        verify(itemRequestRepository, times(0)).save(any());
    }

    @Test
    void shouldNotAddItemRequestWithoutDescription() throws ValidationException {
        ItemRequestCreationDto itemRequestCreationDtoWrong = new ItemRequestCreationDto();

        when(userService.getUserById(any())).thenReturn(userOne);

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        itemRequestService.addItemRequest(1L, itemRequestCreationDtoWrong);
                    }
                }
        );

        Assertions.assertEquals("Не задано описание вещи.", ex.getMessage());
        verify(itemRequestRepository, times(0)).save(any());
    }

    @Test
    void shouldGetRequestByUserId() {
        Optional<ItemRequest> itemRequestOptional = Optional.of(itemRequestOne);

        List<Optional<ItemRequest>> itemRequestsOptional = new ArrayList<>();
        itemRequestsOptional.add(itemRequestOptional);

        List<Item> items = new ArrayList<>();
        items.add(itemTwo);

        when(userService.getUserById(any())).thenReturn(userOne);
        when(itemRequestRepository.findItemRequestsByUserId(1L)).thenReturn(itemRequestsOptional);
        when(itemRepository.findItemsByRequestId(1L)).thenReturn(items);
        List<ItemRequestDto> itemRequestsDto = itemRequestService.getItemRequestsByUserId(1L);

        Assertions.assertNotNull(itemRequestsDto);
        Assertions.assertEquals(itemRequestsDto.size(), 1);
    }

    @Test
    void shouldGetRequestByAnotherUser() {
        Optional<ItemRequest> itemRequestOptional = Optional.of(itemRequestOne);

        List<Optional<ItemRequest>> itemRequestsOptional = new ArrayList<>();
        itemRequestsOptional.add(itemRequestOptional);

        List<Item> items = new ArrayList<>();
        items.add(itemOne);

        when(userService.getUserById(any())).thenReturn(userTwo);
        when(itemRequestRepository.findItemRequestsByAnotherUsers(2L, 0, 20)).thenReturn(itemRequestsOptional);
        when(itemRepository.findItemsByRequestId(1L)).thenReturn(items);
        List<ItemRequestDto> itemRequestsDto = itemRequestService.getRequestsByAnotherUsers(2L, 0, 20);

        Assertions.assertNotNull(itemRequestsDto);
        Assertions.assertEquals(itemRequestsDto.size(), 1);
    }

    @Test
    void shouldNotGetRequestByAnotherUserWithWrongPageQueryFrom() throws ValidationException {
        when(userService.getUserById(any())).thenReturn(userTwo);

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        itemRequestService.getRequestsByAnotherUsers(2L, -1, 20);
                    }
                }
        );
        Assertions.assertEquals("Отрицательный параметр запроса from", ex.getMessage());
    }

    @Test
    void shouldNotGetRequestByAnotherUserWithWrongPageQuerySize() throws ValidationException {
        when(userService.getUserById(any())).thenReturn(userTwo);

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        itemRequestService.getRequestsByAnotherUsers(2L, 0, 0);
                    }
                }
        );
        Assertions.assertEquals("Отрицательный или ноль параметр запроса size", ex.getMessage());
    }

    @Test
    void shouldGetItemRequestById() {
        Optional<ItemRequest> itemRequestOptional = Optional.of(itemRequestOne);

        List<Optional<ItemRequest>> itemRequestsOptional = new ArrayList<>();
        itemRequestsOptional.add(itemRequestOptional);

        List<Item> items = new ArrayList<>();
        items.add(itemOne);

        when(userService.getUserById(any())).thenReturn(userOne);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequestOne));
        when(itemRepository.findItemsByRequestId(1L)).thenReturn(items);
        ItemRequestDto itemRequestsDto = itemRequestService.getItemRequestById(2L, 1L);

        Assertions.assertNotNull(itemRequestsDto);
        Assertions.assertEquals("Дрель помощнее", itemRequestsDto.getDescription());
    }

    @Test
    void shouldNotGetItemRequestByWrongId() throws NotFoundException {
        Optional<ItemRequest> itemRequestOptional = Optional.empty();

        when(userService.getUserById(any())).thenReturn(userOne);
        when(itemRequestRepository.findById(5L)).thenReturn(itemRequestOptional);

        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class, new Executable() {
                    @Override
                    public void execute() {
                        itemRequestService.getItemRequestById(2L, 5L);
                    }
                }
        );
        Assertions.assertEquals("Запрос по id не найден", ex.getMessage());
    }

}
