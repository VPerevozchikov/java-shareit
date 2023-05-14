package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    UserRepository userRepository;
    UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public ResponseEntity<UserDto> addUser(UserCreationDto userCreationDto) throws ValidationException {
        validateNewUser(userCreationDto);
        userRepository.save(
                new User(userCreationDto.getName(), userCreationDto.getEmail()));
        UserDto userDto = userMapper.toDto(userRepository.findByEmailContainingIgnoreCase(userCreationDto.getEmail()));

        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    public void deleteUser(Long id) throws NotFoundException {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь не найден"));
        }
    }

    public ResponseEntity<UserDto> getUserById(Long id) throws NotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            UserDto userDto = userMapper.toDto(user);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь не найден"));
        }
    }

    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = new ArrayList<>();
        users.addAll(userRepository.findAll());
        for (User user : users) {
            usersDto.add(userMapper.toDto(user));
        }
        return new ResponseEntity<>(usersDto, HttpStatus.OK);
    }

    public ResponseEntity<UserDto> updateUser(Long id, UserCreationDto userCreationDto) throws ValidationException {

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            User updateUser = new User();
            updateUser.setId(id);
            updateUser.setName(user.get().getName());
            updateUser.setEmail(user.get().getEmail());
            if (userCreationDto.getName() != null && !userCreationDto.getName().isBlank()) {
                updateUser.setName(userCreationDto.getName());
            }
            if (userCreationDto.getEmail() != null && !userCreationDto.getEmail().isBlank()) {
                updateUser.setEmail(userCreationDto.getEmail());
            }
            userRepository.save(updateUser);
            UserDto userDto = userMapper.toDto(user);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь не найден"));
        }
    }

    public void validateNewUser(UserCreationDto userCreationDto) throws ValidationException {
        if (userCreationDto.getName() == null || userCreationDto.getName().isBlank()) {
            log.info("Ошибка в поле name{}: ", userCreationDto.getName());
            throw new ValidationException("Ошибка в наименовании пользователя.");
        }

        if (userCreationDto.getEmail() == null || userCreationDto.getEmail().isBlank() || !userCreationDto.getEmail().contains("@")) {
            log.info("Ошибка в поле email: {}", userCreationDto.getEmail());
            throw new ValidationException("Ошибка в email.");
        }
    }
}
