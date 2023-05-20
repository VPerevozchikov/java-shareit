package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserDto toDto(User user) {
        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();
        return new UserDto(id, name, email);
    }

    @Override
    public UserDto toDto(Optional<User> user) {
        if (user.isPresent()) {
            Long id = user.get().getId();
            String name = user.get().getName();
            String email = user.get().getEmail();
            return new UserDto(id, name, email);
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь не найден"));
        }
    }

    @Override
    public User toUser(UserCreationDto userCreationDto) {
        String name = userCreationDto.getName();
        String email = userCreationDto.getEmail();
        return new User(name, email);
    }

    @Override
    public User toUser(Optional<User> userFromOptional) {
        User user = new User();
        user.setId(userFromOptional.get().getId());
        user.setName(userFromOptional.get().getName());
        user.setEmail(userFromOptional.get().getEmail());
        return user;
    }
}
