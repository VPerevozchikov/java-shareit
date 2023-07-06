package ru.practicum.shareit.request.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private static final Logger log = LoggerFactory.getLogger(ItemRequestServiceImpl.class);
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;
    private Long count = 0L;

    public ItemRequestServiceImpl(ItemRepository itemRepository,
                                  ItemRequestRepository itemRequestRepository,
                                  ItemMapper itemMapper,
                                  ItemRequestMapper itemRequestMapper,
                                  UserService userService) {

        this.itemRepository = itemRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.itemMapper = itemMapper;
        this.itemRequestMapper = itemRequestMapper;
        this.userService = userService;
    }

    @Override
    public ItemRequestDto addItemRequest(Long userId,
                                         ItemRequestCreationDto itemRequestCreationDto) {
        validateNewItemRequest(userId, itemRequestCreationDto);
        itemRequestCreationDto.setRequestor(userId);
        itemRequestCreationDto.setCreated(LocalDateTime.now());

        ItemRequest newItemRequest = itemRequestMapper.toItemRequest(itemRequestCreationDto);
        itemRequestRepository.save(newItemRequest);
        count++;

        List<Item> items = itemRepository.findItemsByRequestId(count);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemMapper.toDto(item));
        }

        return itemRequestMapper.toDto(itemRequestRepository.findById(count), itemsDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getItemRequestsByUserId(Long userId) {
        User user = userService.getUserById(userId);
        List<Optional<ItemRequest>> itemRequests = itemRequestRepository.findItemRequestsByUserId(userId);

        return getListOfItemsRequestDtoFromOptionalListOfItemsRequest(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getRequestsByAnotherUsers(Long userId, Integer from, Integer size) {
        validateRequestOnGetRequestsByAnotherUsers(userId, from, size);

        List<Optional<ItemRequest>> itemRequests = itemRequestRepository.findItemRequestsByAnotherUsers(userId, from, size);

        return getListOfItemsRequestDtoFromOptionalListOfItemsRequest(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(Long userId, Long itemRequestId) {
        validateRequestOnGetRequestById(userId, itemRequestId);
        List<ItemDto> items = new ArrayList<>();
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemRequestId);

        if (itemRequest.isPresent()) {
            List<Item> itemsFromRepository = itemRepository.findItemsByRequestId(itemRequest.get().getId());
            for (Item item : itemsFromRepository) {
                items.add(itemMapper.toDto(item));
            }
        }
        return itemRequestMapper.toDto(itemRequest, items);
    }

    public void validateNewItemRequest(Long userId, ItemRequestCreationDto itemRequestCreationDto) {
        User user = userService.getUserById(userId);

        if (itemRequestCreationDto.getDescription() == null
                || itemRequestCreationDto.getDescription().isBlank()) {
            log.info("Валидация нового запроса. Не задано описание вещи.");
            throw new ValidationException(String.format(
                    "Не задано описание вещи."));
        }
    }

    public void validateRequestOnGetRequestsByAnotherUsers(Long userId, Integer from, Integer size) {
        User user = userService.getUserById(userId);

        if (from < 0) {
            log.info("Валидация запроса на получение запросов других пользователей.");
            throw new ValidationException(String.format(
                    "Отрицательный параметр запроса from"));
        }
        if (size <= 0) {
            log.info("Валидация запроса на получение запросов других пользователей.");
            throw new ValidationException(String.format(
                    "Отрицательный или ноль параметр запроса size"));
        }
    }

    public void validateRequestOnGetRequestById(Long userId, Long itemRequestId) {
        User user = userService.getUserById(userId);
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemRequestId);

        if (!itemRequest.isPresent()) {
            log.info("Валидация запроса на получение запросов других пользователей.");
            throw new NotFoundException(String.format(
                    "Запрос по id не найден"));
        }
    }

    List<ItemRequestDto> getListOfItemsRequestDtoFromOptionalListOfItemsRequest(List<Optional<ItemRequest>> itemRequestsOptional) {
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        for (Optional<ItemRequest> itemRequestOptional : itemRequestsOptional) {
            List<ItemDto> items = new ArrayList<>();
            if (itemRequestOptional.isPresent()) {
                List<Item> itemsFromRepository = itemRepository.findItemsByRequestId(itemRequestOptional.get().getId());
                for (Item item : itemsFromRepository) {
                    items.add(itemMapper.toDto(item));
                }
            }
            itemRequestsDto.add(itemRequestMapper.toDto(itemRequestOptional, items));
        }
        return itemRequestsDto;
    }
}
