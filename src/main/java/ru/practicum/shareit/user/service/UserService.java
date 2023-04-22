package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;
    private Long userId = 0L;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) throws ValidationException {
        validateNewUser(user);
        user.setId(++userId);
        userStorage.addUser(user);
        return user;
    }

    public void deleteUser(Long id) throws NotFoundException {
        if (userStorage.getUsers().containsKey(id)) {
            userStorage.deleteUser(id);
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь c id %s не найден", id));
        }
    }

    public User getUserById(Long id) throws NotFoundException {
        if (userStorage.getUsers().containsKey(id)) {
            return userStorage.getUsers().get(id);
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь c id %s не найден", id));
        }
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers().values());
    }

    public User updateUser(Long id, User user) throws ValidationException {
        validateUpdateUser(id, user);

        User updateUser = userStorage.getUsers().get(id);
        userStorage.deleteUser(id);
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (!user.getEmail().isBlank() || user.getEmail().contains("@")) {
                updateUser.setEmail(user.getEmail());
            } else {
                log.info("Ошибка в поле email: {}", user.getEmail());
                throw new ValidationException("Ошибка в email.");
            }
        }
        userStorage.addUser(updateUser);

        return updateUser;
    }

    public void validateNewUser(User user) throws ValidationException {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Ошибка в поле name{}: ", user.getName());
            throw new ValidationException("Ошибка в наименовании пользователя.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Ошибка в поле email: {}", user.getEmail());
            throw new ValidationException("Ошибка в email.");
        }

        for (User userFromStorage : userStorage.getUsers().values()) {
            if (userFromStorage.getEmail().equals(user.getEmail())) {
                log.info("email уже существует: {}", user.getEmail());
                throw new EmailDuplicateException("email уже существует.");
            }
        }
    }

    public void validateUpdateUser(Long id, User user) throws ValidationException {
        if (!userStorage.getUsers().containsKey(id)) {
            log.info("Пользователь с id {} не найден.", id);
            throw new ValidationException("Пользователь с id {} не найден.");
        }
        if (user.getEmail() != null) {
            for (User userFromStorage : userStorage.getUsers().values()) {
                if (userFromStorage.getEmail().equals(user.getEmail())) {
                    if (userFromStorage.getId().equals(id)) {
                        log.info("email уже существует: {}", user.getEmail());
                        throw new EmailDuplicateException("email уже существует.");
                    }
                }
            }
        }
    }
}
