package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestMapper {

    ItemRequestDto toDto(Optional<ItemRequest> itemRequest, List<ItemDto> items);

    ItemRequest toItemRequest(ItemRequestCreationDto itemRequestCreationDto);

    ItemRequest toItemRequest(Optional<ItemRequest> itemRequest);
}
