package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Transactional
@Service
public class ItemService {
    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    ItemRepository itemRepository;
    CommentRepository commentRepository;
    ItemMapper itemMapper;
    BookingMapper bookingMapper;
    UserService userService;
    BookingService bookingService;
    Long countComment = 0L;
    Comparator<ItemDto> comparator = new Comparator<ItemDto>() {
        @Override
        public int compare(ItemDto i1, ItemDto i2) {
            return i1.getId().compareTo(i2.getId());
        }
    };

    public ItemService(ItemRepository itemRepository,
                       CommentRepository commentRepository,
                       ItemMapper itemMapper,
                       BookingMapper bookingMapper,
                       UserService userService,
                       BookingService bookingService) {

        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.itemMapper = itemMapper;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    public ItemDto addItem(Long userId, ItemCreationDto itemCreationDto) throws ValidationException {

        validate(userId, itemCreationDto);
        itemCreationDto.setUser(userService.getUserById(userId));
        itemRepository.save(itemMapper.toItem(itemCreationDto));
        ItemDto itemDto = itemMapper.toDto(itemRepository.findByNameContainingIgnoreCase(itemCreationDto.getName()));
        return itemDto;
    }

    public ItemDto getItemById(Long userId, Long id) {
        User user = userService.getUserById(userId);
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {

            ItemDto itemDto = itemMapper.toDto(item);
            List<Booking> listOfBookingsByItemId = bookingService.getBookingsByItemId(id);
            List<Booking> listOfBookings = new ArrayList<>();
            for (Booking booking : listOfBookingsByItemId) {
                if (user.getId().equals(item.get().getUser().getId())) {
                    listOfBookings.add(booking);
                }
            }

            Booking lastBooking = listOfBookings.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .filter(booking -> booking.getStatus().equals(StatusType.APPROVED))
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            Booking nextBooking = listOfBookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .filter(booking -> booking.getStatus().equals(StatusType.APPROVED))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            if (lastBooking != null) {
                itemDto.setLastBooking(bookingMapper.toDto(lastBooking,
                        itemMapper.toItem(itemRepository.findById(lastBooking.getItemId()))));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking != null) {
                itemDto.setNextBooking(bookingMapper.toDto(nextBooking,
                        itemMapper.toItem(itemRepository.findById(nextBooking.getItemId()))));
            } else {
                itemDto.setNextBooking(null);
            }

            List<Comment> comments = commentRepository.findCommentsByItemId(itemDto.getId());
            List<CommentDto> commentsDto = new ArrayList<>();

            for (Comment comment : comments) {
                CommentDto commentDto = CommentMapper.toDto(comment);
                commentDto.setAuthorName(comment.getAuthor().getName());
                commentsDto.add(commentDto);
            }

            itemDto.setComments(commentsDto);
            return itemDto;
        } else {
            throw new NotFoundException(String.format(
                    "Вещь не найдена"));
        }
    }

    public void deleteItem(Long id) throws NotFoundException {
        Optional<Item> item = itemRepository.findById(id);

        if (item.isPresent()) {
            itemRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format(
                    "Вещь не найдена"));
        }
    }

    public Set<ItemDto> getItemsByUserId(Long userId) {
        User user = userService.getUserById(userId);

        List<Item> items = itemRepository.findItemsByUser(user);
        Set<ItemDto> allItemsDtoSortedById = new TreeSet<>(comparator);
        for (Item item : items) {
            ItemDto itemDto = itemMapper.toDto(item);
            List<Booking> listOfBookings = bookingService.getBookingsByItemId(item.getId());

            Booking lastBooking = listOfBookings.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .filter(booking -> booking.getStatus().equals(StatusType.APPROVED))
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            Booking nextBooking = listOfBookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .filter(booking -> booking.getStatus().equals(StatusType.APPROVED))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            if (lastBooking != null) {
                itemDto.setLastBooking(bookingMapper.toDto(lastBooking,
                        itemMapper.toItem(itemRepository.findById(lastBooking.getItemId()))));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking != null) {
                itemDto.setNextBooking(bookingMapper.toDto(nextBooking,
                        itemMapper.toItem(itemRepository.findById(nextBooking.getItemId()))));
            } else {
                itemDto.setNextBooking(null);
            }

            List<Comment> comments = commentRepository.findCommentsByItemId(itemDto.getId());
            List<CommentDto> commentsDto = new ArrayList<>();

            for (Comment comment : comments) {
                CommentDto commentDto = CommentMapper.toDto(comment);
                commentDto.setAuthorName(comment.getAuthor().getName());
                commentsDto.add(commentDto);
            }

            itemDto.setComments(commentsDto);

            allItemsDtoSortedById.add(itemDto);
        }
        return allItemsDtoSortedById;
    }

    public ItemDto updateItem(Long userId, Long id, ItemCreationDto itemCreationDto) throws ValidationException {

        User user = userService.getUserById(userId);
        Optional<Item> item = itemRepository.findById(id);

        if (item.isPresent()) {
            Item updateItem = new Item();
            updateItem.setId(id);
            updateItem.setUser(item.get().getUser());
            updateItem.setRequestId(item.get().getRequestId());

            if (itemCreationDto.getName() != null) {
                updateItem.setName(itemCreationDto.getName());
            } else {
                updateItem.setName(item.get().getName());
            }

            if (itemCreationDto.getDescription() != null) {
                updateItem.setDescription(itemCreationDto.getDescription());
            } else {
                updateItem.setDescription(item.get().getDescription());
            }

            if (itemCreationDto.getAvailable() != null) {
                updateItem.setAvailable(itemCreationDto.getAvailable());
            } else {
                updateItem.setAvailable(item.get().getAvailable());
            }

            itemRepository.save(updateItem);
            Optional<Item> itemFromRepository = itemRepository.findById(id);
            return itemMapper.toDto(itemFromRepository);
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь и/или вещь не найдены."));
        }
    }

    public List<ItemDto> searchItem(Long userId, String text) {

        User user = userService.getUserById(userId);
        List<ItemDto> itemsDto = new ArrayList<>();
        if (text.isBlank()) {
            return itemsDto;
        } else {
            List<Optional<Item>> itemsFromRepository = itemRepository.searchItemsByText(text);
            for (Optional<Item> item : itemsFromRepository) {
                if (item.get().getAvailable()) {
                    itemsDto.add(itemMapper.toDto(item));
                }
            }
            return itemsDto;
        }
    }

    public CommentDto addComment(Long userId, Long itemId, CommentCreationDto commentCreationDto) throws ValidationException,
            NotFoundException {
        validateComment(userId, itemId, commentCreationDto);
        commentCreationDto.setItemId(itemId);
        commentCreationDto.setAuthor(userService.getUserById(userId));
        commentRepository.save(CommentMapper.toComment(commentCreationDto));
        countComment++;
        CommentDto commentDto = CommentMapper.toDto(commentRepository.findById(countComment));
        return commentDto;
    }

    public void validate(Long userId, ItemCreationDto itemCreationDto) throws ValidationException {
        if (itemCreationDto.getName() == null || itemCreationDto.getName().isBlank()) {
            log.info("Поле name отсутствует или пусто.");
            throw new ValidationException("Поле name отсутствует или пусто.");
        }

        if (itemCreationDto.getDescription() == null || itemCreationDto.getDescription().isBlank()) {
            log.info("Поле description отсутствует или пусто.");
            throw new ValidationException("Поле description отсутствует или пусто.");
        }

        if (itemCreationDto.getAvailable() == null) {
            log.info("Поле available отсутствует.");
            throw new ValidationException("Поле available отсутствует.");
        }
        User user = userService.getUserById(userId);
    }

    public void validateComment(Long userId, Long itemId, CommentCreationDto commentCreationDto) throws ValidationException,
            NotFoundException {
        if (commentCreationDto.getText() == null || commentCreationDto.getText().isBlank()) {
            log.info("Поле text отсутствует или пусто.");
            throw new ValidationException("Поле text отсутствует или пусто.");
        }

        Optional<Item> item = itemRepository.findById(itemId);

        if (!item.isPresent()) {
            log.info("Вещь не найдена.");
            throw new NotFoundException(String.format(
                    "Вещь не найдена."));
        }

        String state = "PAST";
        List<BookingDto> bookingsDto = bookingService.getBookingsByBookerId(userId, state, 0, 100);
        boolean isUserEndUsedItem = false;

        if (!bookingsDto.isEmpty()) {
            isUserEndUsedItem = true;
        }

        if (!isUserEndUsedItem) {
            log.info("Пользователь находится в процессе использования вещи и не может оставить комментарий." +
                    "Или пользователь не бронировал вещь и не может оставить комментарий.");
            throw new ValidationException("Пользователь находится в процессе использования вещи и не может оставить комментарий." +
                    "Или пользователь не бронировал вещь и не может оставить комментарий.");
        }
    }
}