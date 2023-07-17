package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> postItem(@Valid @RequestBody ItemCreationDto itemCreationDto,
                                           @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Creating item {}, userId={}", itemCreationDto, ownerId);
        return itemClient.postItem(itemCreationDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> putItem(@RequestBody ItemCreationDto itemCreationDto, @PathVariable int itemId,
                                          @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Update item id={}, userId={}", itemId, ownerId);
        return itemClient.putItem(itemCreationDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable int itemId,
                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get item, itemId={}, userId = {}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get items with userId={}, from={}, size={}", ownerId, from, size);
        return itemClient.getItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(value = "text", required = true) String text) {
        log.info("Search items with text={}", text);
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable int itemId,
                                                @RequestHeader("X-Sharer-User-Id") int authorId,
                                                @Valid @RequestBody CommentCreationDto commentCreationDto) {
        log.info("Creating comment {}, itemId={}, authorId={}", commentCreationDto, itemId, authorId);
        return itemClient.postComment(commentCreationDto, itemId, authorId);
    }

}