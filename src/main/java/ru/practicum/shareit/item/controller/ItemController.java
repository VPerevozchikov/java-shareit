package ru.practicum.shareit.item.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.ValidationException;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    @GetMapping("/{id}")
    public Item getItemById(@PathVariable long id) {
        log.info("Запрос на получение вещи по id: {}", id);
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<Item> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение вещей пользователя по id: {}", userId);
        return itemService.getItemsByUserId(userId);
    }

    @PostMapping
    public Item addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                        @RequestBody Item item) throws ValidationException {
        log.info("Запрос на создание вещи: {}", item);
        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{id}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long id, @RequestBody Item item) throws ValidationException {
        log.info("Запрос на обновление вещи: {}", item);
        return itemService.updateItem(userId, id, item);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable long id) {
        log.info("Запрос на удаление вещи по id: {}", id);
        itemService.deleteItem(id);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestParam(value = "text", required = true) String text) throws ValidationException {
        return itemService.searchItem(userId, text);
    }
}