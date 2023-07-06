package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest()
public class RequestServiceIntegrationTest {

    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private UserCreationDto userCreationDtoOne;
    private ItemRequestCreationDto itemRequestCreationDtoOne;
    private ItemCreationDto itemCreationDtoOne;

    @BeforeEach
    void setUp() {

        itemRequestCreationDtoOne = new ItemRequestCreationDto();
        itemRequestCreationDtoOne.setDescription("Дрель помощнее");

        userCreationDtoOne = new UserCreationDto();
        userCreationDtoOne.setName("Vlad");
        userCreationDtoOne.setEmail("test@yandex.ru");

        itemCreationDtoOne = new ItemCreationDto();
        itemCreationDtoOne.setDescription("1500Вт");
        itemCreationDtoOne.setAvailable(true);
        itemCreationDtoOne.setName("Дрель");
    }

    @Test
    void getItemRequestByIdTest() {
        UserDto userDtoOne = userService.addUser(userCreationDtoOne);

        User userOne = new User();
        userOne.setId(userDtoOne.getId());
        userOne.setName(userDtoOne.getName());
        userOne.setEmail(userDtoOne.getEmail());

        ItemDto itemDtoOne = itemService.addItem(userOne.getId(), itemCreationDtoOne);

        ItemRequestDto itemRequestDto = itemRequestService.addItemRequest(userDtoOne.getId(),
                itemRequestCreationDtoOne);

        ItemRequestDto itemRequestDtoFromRepository = itemRequestService.getItemRequestById(userDtoOne.getId(),
                itemRequestDto.getId());

        Assertions.assertNotNull(itemRequestDtoFromRepository);
        assertThat(itemRequestDtoFromRepository.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequestDtoFromRepository.getDescription(), equalTo(itemRequestDto.getDescription()));
    }
}
