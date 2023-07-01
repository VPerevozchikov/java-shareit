package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest()

public class UserServiceIntegrationTest {
    private final UserService userService;
    public UserCreationDto userCreationDtoOne;
    public UserCreationDto userCreationDtoTwo;


    @BeforeEach
    void setUp() {
        userCreationDtoOne = new UserCreationDto();
        userCreationDtoOne.setName("Vlad");
        userCreationDtoOne.setEmail("test@yandex.ru");

        userCreationDtoTwo = new UserCreationDto();
        userCreationDtoTwo.setName("Ivan");
        userCreationDtoTwo.setEmail("ivan@yandex.ru");
    }

    @Test
    void getAllUsersTest() {
        UserDto userDtoOne = userService.addUser(userCreationDtoOne);
        UserDto userDtoTwo = userService.addUser(userCreationDtoTwo);

        List<UserDto> newUserDtoList = userService.getUsers();

        assertThat(newUserDtoList.size(), equalTo(2));
        assertThat(newUserDtoList.get(0).getId(), equalTo(userDtoOne.getId()));
        assertThat(newUserDtoList.get(1).getId(), equalTo(userDtoTwo.getId()));
    }

    @Test
    void updateUserTest() {
        UserDto userDtoOne = userService.addUser(userCreationDtoOne);
        UserDto userDtoTwo = userService.addUser(userCreationDtoTwo);

        UserCreationDto userCreationDtoUpdate = new UserCreationDto();
        userCreationDtoUpdate.setName("updated Vlad");

        UserDto updatedUserDto = userService.updateUser(userDtoOne.getId(), userCreationDtoUpdate);

        assertThat(updatedUserDto.getName(), equalTo("updated Vlad"));

        assertThrows(NotFoundException.class,
                () -> userService.updateUser(99L, userCreationDtoUpdate));

        userCreationDtoUpdate.setEmail("ivan@yandex.ru");
        assertThrows(NotFoundException.class,
                () -> userService.updateUser(99L, userCreationDtoUpdate));
    }
}
