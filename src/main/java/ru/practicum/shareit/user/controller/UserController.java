package ru.practicum.shareit.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable long id) {
        log.info("Запрос на получение пользователя по id: {}", id);
        return new ResponseEntity<>(userService.getUserDtoById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserCreationDto userCreationDto) {
        log.info("Запрос на добавление пользователя");
        return new ResponseEntity<>(userService.addUser(userCreationDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        log.info("Запрос на получение всех пользователей");
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable long id,
                                              @RequestBody UserCreationDto userCreationDto) {
        log.info("Запрос на обновление пользователя по id: {}", id);
        return new ResponseEntity<>(userService.updateUser(id, userCreationDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("Запрос на удаление пользователя по id: {}", id);
        userService.deleteUser(id);
    }
}