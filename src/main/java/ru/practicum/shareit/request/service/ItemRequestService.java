package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long userId,
                                  ItemRequestCreationDto itemRequestCreationDto) throws ValidationException,
            NotFoundException;

    List<ItemRequestDto> getItemRequestsByUserId(Long userId) throws ValidationException,
            NotFoundException;

    List<ItemRequestDto> getRequestsByAnotherUsers(Long userId, Integer from, Integer size) throws ValidationException,
            NotFoundException;

    ItemRequestDto getItemRequestById(Long userId, Long itemRequestId) throws ValidationException,
            NotFoundException;
}
