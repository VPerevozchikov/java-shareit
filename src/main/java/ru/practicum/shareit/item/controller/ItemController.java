package ru.practicum.shareit.item.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/items")
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable long id) {
        log.info("Запрос на получение вещи.");
        return new ResponseEntity<>(itemService.getItemById(userId, id), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(value = "text", required = true) String text) {
        log.info("Поисковой запрос");

        return new ResponseEntity<>(itemService.searchItem(userId, text), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Set<ItemDto>> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение вещей пользователя по id: {}", userId);
        return new ResponseEntity<>(itemService.getItemsByUserId(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @Valid @RequestBody ItemCreationDto itemCreationDto) {
        log.info("Запрос на создание вещи.");
        return new ResponseEntity<>(itemService.addItem(userId, itemCreationDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long id,
                                              @RequestBody ItemCreationDto itemCreationDto) {
        log.info("Запрос на обновление вещи.");
        return new ResponseEntity<>(itemService.updateItem(userId, id, itemCreationDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable long id) {
        log.info("Запрос на удаление вещи по id: {}", id);
        itemService.deleteItem(id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long itemId,
                                                 @Valid @RequestBody CommentCreationDto commentCreationDto) {
        log.info("Запрос на создание комментария.");
        return new ResponseEntity<>(itemService.addComment(userId, itemId, commentCreationDto), HttpStatus.OK);
    }
}