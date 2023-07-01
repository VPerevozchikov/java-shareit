package ru.practicum.shareit.request.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final Logger log = LoggerFactory.getLogger(ItemRequestController.class);
    ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestBody ItemRequestCreationDto itemRequestCreationDto)
            throws RuntimeException {
        log.info("Запрос на создание запроса вещи.");
        return new ResponseEntity<>(itemRequestService.addItemRequest(userId, itemRequestCreationDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение списка своих запросов");
        return new ResponseEntity<>(itemRequestService.getItemRequestsByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getRequestsByAnotherUsers(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                          @RequestParam(value = "from",
                                                                                  required = false,
                                                                                  defaultValue = "0") Integer from,
                                                                          @RequestParam(value = "size",
                                                                                  required = false,
                                                                                  defaultValue = "20") Integer size) {
        log.info("Запрос на получение списка запросов других пользователей");
        return new ResponseEntity<>(itemRequestService.getRequestsByAnotherUsers(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @PathVariable long requestId) {
        log.info("Запрос на получение информации о запросе вещи.");
        return new ResponseEntity<>(itemRequestService.getItemRequestById(userId, requestId), HttpStatus.OK);
    }

}
