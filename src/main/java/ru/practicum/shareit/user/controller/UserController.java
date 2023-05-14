package ru.practicum.shareit.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repositories.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    UserService userService;
    UserRepository userRepository;


    public UserController(UserService userService,
                          UserRepository userRepository) {

        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable long id) {
        log.info("Запрос на получение пользователя по id: {}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserCreationDto userCreationDto) throws ValidationException {
        log.info("Запрос на добавление пользователя");
        return userService.addUser(userCreationDto);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        log.info("Запрос на получение всех пользователей");
        return userService.getUsers();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable long id, @RequestBody UserCreationDto userCreationDto) {
        log.info("Запрос на обновление пользователя по id: {}", id);
        return userService.updateUser(id, userCreationDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("Запрос на удаление пользователя по id: {}", id);
        userService.deleteUser(id);
    }
}