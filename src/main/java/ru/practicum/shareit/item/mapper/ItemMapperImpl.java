package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

@Component
public class ItemMapperImpl implements ItemMapper {

    @Override
    public ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setUser(item.getUser());
        itemDto.setRequestId(item.getRequestId());
        return itemDto;
    }

    @Override
    public ItemDto toDto(Optional<Item> item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.get().getId());
        itemDto.setName(item.get().getName());
        itemDto.setDescription(item.get().getDescription());
        itemDto.setAvailable(item.get().getAvailable());
        itemDto.setUser(item.get().getUser());
        itemDto.setRequestId(item.get().getRequestId());
        return itemDto;
    }

    @Override
    public Item toItem(ItemCreationDto itemCreationDto) {
        Item item = new Item();
        item.setName(itemCreationDto.getName());
        item.setDescription(itemCreationDto.getDescription());
        item.setAvailable(itemCreationDto.getAvailable());
        item.setUser(itemCreationDto.getUser());
        item.setRequestId(itemCreationDto.getRequestId());
        return item;
    }

    @Override
    public Item toItem(Optional<Item> itemFromOptional) {
        Item item = new Item();
        item.setId(itemFromOptional.get().getId());
        item.setName(itemFromOptional.get().getName());
        item.setDescription(itemFromOptional.get().getDescription());
        item.setAvailable(itemFromOptional.get().getAvailable());
        item.setUser(itemFromOptional.get().getUser());
        item.setRequestId(itemFromOptional.get().getRequestId());
        return item;
    }
}
