package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

@Component
public class ItemRequestMapperImpl implements ItemRequestMapper {

    @Override
    public ItemRequestDto toDto(ItemRequest itemRequest, List<ItemDto> items) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestor(itemRequest.getRequestor());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    @Override
    public ItemRequestDto toDto(Optional<ItemRequest> itemRequest, List<ItemDto> items) {
        if (itemRequest.isPresent()) {
            ItemRequestDto itemRequestDto = new ItemRequestDto();
            itemRequestDto.setId(itemRequest.get().getId());
            itemRequestDto.setDescription(itemRequest.get().getDescription());
            itemRequestDto.setRequestor(itemRequest.get().getRequestor());
            itemRequestDto.setCreated(itemRequest.get().getCreated());
            itemRequestDto.setItems(items);
            return itemRequestDto;
        } else {
            throw new NotFoundException(String.format(
                    "Запрос не найден"));
        }
    }

    @Override
    public ItemRequest toItemRequest(ItemRequestCreationDto itemRequestCreationDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestCreationDto.getDescription());
        itemRequest.setRequestor(itemRequestCreationDto.getRequestor());
        itemRequest.setCreated(itemRequestCreationDto.getCreated());
        return itemRequest;
    }

    @Override
    public ItemRequest toItemRequest(Optional<ItemRequest> itemRequestFromOptional) {
        if (itemRequestFromOptional.isPresent()) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemRequestFromOptional.get().getId());
            itemRequest.setDescription(itemRequestFromOptional.get().getDescription());
            itemRequest.setRequestor(itemRequestFromOptional.get().getRequestor());
            itemRequest.setCreated(itemRequestFromOptional.get().getCreated());
            return itemRequest;
        } else {
            throw new NotFoundException(String.format(
                    "Запрос не найден"));
        }
    }
}
