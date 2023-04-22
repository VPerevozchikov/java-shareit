package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {
    private static final Logger log = LoggerFactory.getLogger(ItemService.class);
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private Long itemId = 0L;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage) {

        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public Item addItem(Long userId, Item item) throws ValidationException {
        validate(userId, item);
        item.setId(++itemId);
        item.setOwener(userId);
        itemStorage.addItem(item);
        return item;
    }

    public void deleteItem(Long id) throws NotFoundException {
        if (itemStorage.getItems().containsKey(id)) {
            itemStorage.deleteItem(id);
        } else {
            throw new NotFoundException(String.format(
                    "Вещь c id %s не найдена", id));
        }
    }

    public Item getItemById(Long id) {
        if (itemStorage.getItems().containsKey(id)) {
            Item item = itemStorage.getItems().get(id);
            return item;
        } else {
            throw new NotFoundException(String.format(
                    "Вещь c id %s не найдена", id));
        }
    }

    public List<Item> getItemsByUserId(Long userId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException(String.format(
                    "Пользователь с id %s не найден", userId));
        } else {
            List<Item> items = new ArrayList<>();
            for (Item item : itemStorage.getItems().values()) {
                if (item.getOwener().equals(userId)) {
                    items.add(item);
                }
            }
            return items;
        }
    }

    public Item updateItem(Long userId, Long id, Item item) throws ValidationException {
        validateUpdateItem(userId, id, item);

        Item itemForUpdate = itemStorage.getItems().get(id);
        itemStorage.deleteItem(id);
        if (item.getName() != null) {
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemForUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
        if (item.getOwener() != null) {
            itemForUpdate.setOwener(item.getOwener());
        }

        itemStorage.addItem(itemForUpdate);
        return itemForUpdate;
    }

    public List<Item> searchItem(Long userId, String text) {
        String lowerCaseText = text.toLowerCase();
        List<Item> items = new ArrayList<>();
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException(String.format(
                    "Пользователь с id %s не найден", userId));
        } else if (text.isBlank()) {
            return items;
        } else {
            for (Item item : itemStorage.getItems().values()) {
                if (item.getAvailable()) {
                    if (item.getName().toLowerCase().contains(lowerCaseText)
                            || item.getDescription().toLowerCase().contains(lowerCaseText)) {
                        items.add(item);
                    }
                }
            }
        }
        return items;
    }

    public void validate(Long id, Item item) throws ValidationException {
        if (item.getName() == null || item.getName().isBlank()) {
            log.info("Поле name отсутствует или пусто.");
            throw new ValidationException("Поле name отсутствует или пусто.");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.info("Поле description отсутствует или пусто.");
            throw new ValidationException("Поле description отсутствует или пусто.");
        }

        if (item.getAvailable() == null) {
            log.info("Поле available отсутствует.");
            throw new ValidationException("Поле available отсутствует.");
        }

        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException(String.format(
                    "Хозяин вещи c id %s не найден", id));
        }
    }

    public void validateUpdateItem(Long userId, Long id, Item item) throws ValidationException {

        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException(String.format(
                    "Хозяин вещи c id %s не найден", userId));
        }

        if (!itemStorage.getItems().containsKey(id)) {
            throw new NotFoundException(String.format(
                    "Вещь c id %s не найдена", id));
        }

        Item itemFromStorage = itemStorage.getItems().get(id);
        if (itemFromStorage.getOwener().equals(userId)) {
            throw new NotFoundException(String.format(
                    "Вещь c id %s не принадлежит User c id %s", id, userId));
        }
    }
}