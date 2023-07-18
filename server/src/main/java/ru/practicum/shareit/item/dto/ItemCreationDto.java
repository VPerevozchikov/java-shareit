package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ItemCreationDto {

    String name;
    String description;
    Boolean available;
    User user;
    Long requestId;
}
