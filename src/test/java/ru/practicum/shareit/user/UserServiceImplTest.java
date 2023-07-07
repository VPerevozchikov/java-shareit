package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserService userService;

    private User userOne;
    private User userTwo;

    private UserCreationDto userCreationDtoOne;
    private UserCreationDto userCreationDtoTwo;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = new UserMapperImpl();

        userService = new UserService(userRepository,
                userMapper);

        userCreationDtoOne = new UserCreationDto();
        userCreationDtoOne.setName("Vlad");
        userCreationDtoOne.setEmail("test@yandex.ru");

        userOne = userMapper.toUser(userCreationDtoOne);
        userOne.setId(1L);

    }

    @Test
    void shouldAddUser() {
        when(userRepository.findByEmailContainingIgnoreCase(any())).thenReturn(userOne);
        UserDto userDto = userService.addUser(userCreationDtoOne);

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(userDto.getId(), 1);
        Assertions.assertEquals(userDto.getName(), "Vlad");
        Assertions.assertEquals(userDto.getEmail(), "test@yandex.ru");
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldNotAddUserWithNotDeclareName() throws ValidationException {
        UserCreationDto userCreationDtoWrongName = new UserCreationDto();
        userCreationDtoWrongName.setEmail("test@yandex.ru");

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        userService.addUser(userCreationDtoWrongName);
                    }
                }
        );

        Assertions.assertEquals("Ошибка в наименовании пользователя.", ex.getMessage());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void shouldNotAddUserWithEmptyName() throws ValidationException {
        UserCreationDto userCreationDtoWrongName = new UserCreationDto();
        userCreationDtoWrongName.setName("   ");
        userCreationDtoWrongName.setEmail("test@yandex.ru");

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        userService.addUser(userCreationDtoWrongName);
                    }
                }
        );

        Assertions.assertEquals("Ошибка в наименовании пользователя.", ex.getMessage());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void shouldNotAddUserWithNotDeclareEmail() throws ValidationException {
        UserCreationDto userCreationDtoWrongEmail = new UserCreationDto();
        userCreationDtoWrongEmail.setName("Vlad");

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        userService.addUser(userCreationDtoWrongEmail);
                    }
                }
        );

        Assertions.assertEquals("Ошибка в email.", ex.getMessage());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void shouldNotAddUserWithEmptyEmail() throws ValidationException {
        UserCreationDto userCreationDtoWrongEmail = new UserCreationDto();
        userCreationDtoWrongEmail.setName("Vlad");
        userCreationDtoWrongEmail.setEmail("   ");

        ValidationException ex = Assertions.assertThrows(
                ValidationException.class, new Executable() {
                    @Override
                    public void execute() {
                        userService.addUser(userCreationDtoWrongEmail);
                    }
                }
        );

        Assertions.assertEquals("Ошибка в email.", ex.getMessage());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void shouldDeleteUserById() {

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));

        userService.deleteUser(userOne.getId());

        verify(userRepository, times(1)).deleteById(any());
    }

    @Test
    void shouldNotDeleteUserByWrongId() throws NotFoundException {
        Optional<User> user = Optional.empty();
        when(userRepository.findById(any())).thenReturn(user);

        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class, new Executable() {
                    @Override
                    public void execute() {
                        userService.deleteUser(any());
                    }
                }
        );
        Assertions.assertEquals("Пользователь не найден", ex.getMessage());
        verify(userRepository, times(0)).deleteById(any());
    }

    @Test
    void shouldGetUserDtoById() {
        when(userRepository.findById(userOne.getId())).thenReturn(Optional.of(userOne));
        UserDto userDto = userService.getUserDtoById(1L);

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(userDto.getId(), 1);
        Assertions.assertEquals(userDto.getName(), "Vlad");
        Assertions.assertEquals(userDto.getEmail(), "test@yandex.ru");
    }

    @Test
    void shouldNotGetUserDtoByWrongId() throws NotFoundException {
        Optional<User> user = Optional.empty();
        when(userRepository.findById(any())).thenReturn(user);

        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class, new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        userService.getUserDtoById(1L);
                    }
                }
        );

        Assertions.assertEquals("Пользователь не найден", ex.getMessage());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void shouldGetUserById() {
        when(userRepository.findById(userOne.getId())).thenReturn(Optional.of(userOne));
        User user = userService.getUserById(1L);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getId(), 1);
        Assertions.assertEquals(user.getName(), "Vlad");
        Assertions.assertEquals(user.getEmail(), "test@yandex.ru");
    }

    @Test
    void shouldNotGetUserByWrongId() throws NotFoundException {
        Optional<User> user = Optional.empty();
        when(userRepository.findById(any())).thenReturn(user);

        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class, new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        userService.getUserById(1L);
                    }
                }
        );

        Assertions.assertEquals("Пользователь не найден", ex.getMessage());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void shouldGetUsers() {
        userCreationDtoTwo = new UserCreationDto();
        userCreationDtoTwo.setName("Ivan");
        userCreationDtoTwo.setEmail("ivan@yandex.ru");

        userTwo = userMapper.toUser(userCreationDtoTwo);
        userTwo.setId(2L);
        List<User> listOfUsers = new ArrayList<>();
        listOfUsers.add(userOne);
        listOfUsers.add(userTwo);

        when(userRepository.findAll()).thenReturn(listOfUsers);
        List<UserDto> listOfUserDto = userService.getUsers();

        Assertions.assertNotNull(listOfUserDto);
        Assertions.assertEquals(listOfUserDto.size(), 2);
    }

    @Test
    void shouldUpdateUser() {

        UserCreationDto userCreationDtoUpdate = new UserCreationDto();
        userCreationDtoUpdate.setName("Vladislav");
        userCreationDtoUpdate.setEmail("testUpdate@yandex.ru");

        when(userRepository.findById(any())).thenReturn(Optional.of(userOne));
        UserDto userDtoUpdate = userService.updateUser(1L, userCreationDtoUpdate);

        Assertions.assertNotNull(userDtoUpdate);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldNoteUpdateUserByWrongId() throws NotFoundException {

        Optional<User> user = Optional.empty();

        UserCreationDto userCreationDtoUpdate = new UserCreationDto();
        userCreationDtoUpdate.setName("Vladislav");
        userCreationDtoUpdate.setEmail("testUpdate@yandex.ru");

        when(userRepository.findById(any())).thenReturn(user);

        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class, new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        userService.updateUser(1L, userCreationDtoUpdate);
                    }
                }
        );
        Assertions.assertEquals("Пользователь не найден", ex.getMessage());
        verify(userRepository, times(0)).save(any());
    }
}
