package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    UserCreationDto userCreationDtoOne;
    UserMapper userMapper;
    User userOne;
    UserDto userDtoOne;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();

        userCreationDtoOne = new UserCreationDto();
        userCreationDtoOne.setName("Vlad");
        userCreationDtoOne.setEmail("test@yandex.ru");

        userOne = userMapper.toUser(userCreationDtoOne);
        userOne.setId(1L);

        userDtoOne = userMapper.toDto(userOne);
    }

    @SneakyThrows
    @Test
    void getUserByIdTest() {

        long userId = 1L;

        when(userService.getUserDtoById(userId))
                .thenReturn(userDtoOne);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDtoOne.getName())));
        verify(userService).getUserDtoById(userId);
    }

    @Test
    @SneakyThrows
    void addUserTest() {

        when(userService.addUser(any()))
                .thenReturn(userDtoOne);

        String result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userCreationDtoOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.name", is(userDtoOne.getName())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService).addUser(any());
        assertThat(objectMapper.writeValueAsString(userDtoOne), equalTo(result));
    }

    @SneakyThrows
    @Test
    void getUsersTest() {
        List<UserDto> users = new ArrayList<>();
        users.add(userDtoOne);

        when(userService.getUsers())
                .thenReturn(users);

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService).getUsers();
        assertThat(objectMapper.writeValueAsString(users), equalTo(result));
    }

    @SneakyThrows
    @Test
    void updateUsersTest() {

        when(userService.updateUser(any(), any()))
                .thenReturn(userDtoOne);

        when(userService.getUserById(any()))
                .thenReturn(userOne);

        String result = mockMvc.perform(patch("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(userCreationDtoOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name", is(userDtoOne.getName())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService).updateUser(any(), any());
        assertThat(objectMapper.writeValueAsString(userDtoOne), equalTo(result));
    }

    @SneakyThrows
    @Test
    void deleteUserTest() {

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userService).deleteUser(any());
    }
}
