package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Map;

public interface ItemStorage {
    void addItem(Item item);

    void deleteItem(Long id);

    Map<Long, Item> getItems();
}
