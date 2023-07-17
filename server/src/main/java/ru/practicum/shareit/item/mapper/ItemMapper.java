package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public interface ItemMapper {
    ItemDto toDto(Item item);

    Item toItem(ItemCreationDto itemCreationDto);

    ItemDto toDto(Optional<Item> item);

    Item toItem(Optional<Item> item);
}