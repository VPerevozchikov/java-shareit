package ru.practicum.shareit.item.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repositories.UserRepository;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/items")
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemService itemService;

    public ItemController(ItemRepository itemRepository,
                          UserRepository userRepository,
                          ItemService itemService) {

        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable long id) {
        log.info("Запрос на получение вещи.");
        return itemService.getItemById(userId, id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(value = "text", required = true) String text) {
        log.info("Поисковой запрос");

        return itemService.searchItem(userId, text);
    }

    @GetMapping
    public ResponseEntity<Set<ItemDto>> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение вещей пользователя по id: {}", userId);
        return itemService.getItemsByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody ItemCreationDto itemCreationDto) throws ValidationException {
        log.info("Запрос на создание вещи.");
        return itemService.addItem(userId, itemCreationDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long id, @RequestBody ItemCreationDto itemCreationDto) throws ValidationException {
        log.info("Запрос на обновление вещи.");
        return itemService.updateItem(userId, id, itemCreationDto);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable long id) {
        log.info("Запрос на удаление вещи по id: {}", id);
        itemService.deleteItem(id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long itemId,
                                                 @RequestBody CommentCreationDto commentCreationDto) throws ValidationException {
        log.info("Запрос на создание комментария.");
        return itemService.addComment(userId, itemId, commentCreationDto);
    }
}