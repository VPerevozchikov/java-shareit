package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Transactional
@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto addUser(UserCreationDto userCreationDto) {
        validateNewUser(userCreationDto);
        userRepository.save(
                new User(userCreationDto.getName(), userCreationDto.getEmail()));
        UserDto userDto = userMapper.toDto(userRepository.findByEmailContainingIgnoreCase(userCreationDto.getEmail()));

        return userDto;
    }

    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь не найден"));
        }
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            UserDto userDto = userMapper.toDto(user);
            return userDto;
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь не найден"));
        }
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userMapper.toUser(userOptional);
            return user;
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь не найден"));
        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = new ArrayList<>();
        users.addAll(userRepository.findAll());
        for (User user : users) {
            usersDto.add(userMapper.toDto(user));
        }
        return usersDto;
    }

    public UserDto updateUser(Long id, UserCreationDto userCreationDto) {

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
            UserDto userDto = userMapper.toDto(userRepository.findById(id));
            return userDto;
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь не найден"));
        }
    }

    public void validateNewUser(UserCreationDto userCreationDto) {
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
