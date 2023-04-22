package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapperImpl implements ItemMapper {

    @Override
    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setOwener(item.getOwener());
        if (itemDto.getRequest() != null) {
            itemDto.setRequest(itemDto.getRequest());
        } else {
            itemDto.setRequest(null);
        }
        return itemDto;
    }
}
