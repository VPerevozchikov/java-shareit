package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Map;

public interface UserStorage {
    void addUser(User user);

    void deleteUser(Long id);

    Map<Long, User> getUsers();
}