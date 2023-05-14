package ru.practicum.shareit.user.mapper;


import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserMapper {
    UserDto toDto(User user);

    UserDto toDto(Optional<User> user);

    User toUser(UserCreationDto userCreationDto);

    User toUser(Optional<User> user);
}
