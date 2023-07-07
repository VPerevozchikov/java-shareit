package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long userId,
                                  ItemRequestCreationDto itemRequestCreationDto);

    List<ItemRequestDto> getItemRequestsByUserId(Long userId);

    List<ItemRequestDto> getRequestsByAnotherUsers(Long userId, Integer from, Integer size);

    ItemRequestDto getItemRequestById(Long userId, Long itemRequestId);
}
