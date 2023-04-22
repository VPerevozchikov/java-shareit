package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryItemStorage implements ItemStorage {
    Map<Long, Item> items = new HashMap<>();

    @Override
    public void addItem(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public void deleteItem(Long id) {
        items.remove(id);
    }

    @Override
    public Map<Long, Item> getItems() {
        return items;
    }
}
