package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
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
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ItemService {
    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    ItemRepository itemRepository;
    BookingRepository bookingRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;
    ItemMapper itemMapper;
    BookingMapper bookingMapper;
    UserMapper userMapper;
    CommentMapper commentMapper;
    Long countComment = 0L;
    Comparator<ItemDto> comparator = new Comparator<ItemDto>() {
        @Override
        public int compare(ItemDto i1, ItemDto i2) {
            return i1.getId().compareTo(i2.getId());
        }
    };


    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository,
                       BookingRepository bookingRepository, CommentRepository commentRepository,
                       ItemMapper itemMapper, UserMapper userMapper,
                       BookingMapper bookingMapper, CommentMapper commentMapper) {

        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
        this.bookingMapper = bookingMapper;
        this.commentMapper = commentMapper;
    }

    public ResponseEntity<ItemDto> addItem(Long userId, ItemCreationDto itemCreationDto) throws ValidationException {

        validate(userId, itemCreationDto);
        itemCreationDto.setUser(userMapper.toUser(userRepository.findById(userId)));
        itemRepository.save(itemMapper.toItem(itemCreationDto));
        ItemDto itemDto = itemMapper.toDto(itemRepository.findByNameContainingIgnoreCase(itemCreationDto.getName()));
        return new ResponseEntity<>(itemDto, HttpStatus.CREATED);
    }

    public ResponseEntity<ItemDto> getItemById(Long userId, Long id) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {

            ItemDto itemDto = itemMapper.toDto(item);
            List<Optional<Booking>> listOfBookingsOptional = bookingRepository.findBookingByItemIdOrderByStartDesc(id);
            List<Booking> listOfBookings = new ArrayList<>();
            for (Optional<Booking> booking : listOfBookingsOptional) {
                if (user.get().getId() == item.get().getUser().getId()) {
                    listOfBookings.add(bookingMapper.toBooking(booking));
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
                itemDto.setLastBooking(bookingMapper.toDto(lastBooking));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking != null) {
                itemDto.setNextBooking(bookingMapper.toDto(nextBooking));
            } else {
                itemDto.setNextBooking(null);
            }

            List<Comment> comments = commentRepository.findCommentsByItemId(itemDto.getId());
            List<CommentDto> commentsDto = new ArrayList<>();

            for (Comment comment : comments) {
                CommentDto commentDto = commentMapper.toDto(comment);
                commentDto.setAuthorName(comment.getAuthor().getName());
                commentsDto.add(commentDto);
            }

            itemDto.setComments(commentsDto);
            return new ResponseEntity<>(itemDto, HttpStatus.OK);
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

    public ResponseEntity<Set<ItemDto>> getItemsByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        LocalDateTime localDateTime = LocalDateTime.now();

        if (user.isPresent()) {
            List<Item> items = itemRepository.findItemsByUser(userMapper.toUser(user));
            Set<ItemDto> allItemsDtoSortedById = new TreeSet<>(comparator);
            for (Item item : items) {
                ItemDto itemDto = itemMapper.toDto(item);
                List<Optional<Booking>> listOfBookingsOptional = bookingRepository.findBookingByItemIdOrderByStartDesc(item.getId());
                List<Booking> listOfBookings = new ArrayList<>();
                for (Optional<Booking> booking : listOfBookingsOptional) {
                    listOfBookings.add(bookingMapper.toBooking(booking));
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
                    itemDto.setLastBooking(bookingMapper.toDto(lastBooking));
                } else {
                    itemDto.setLastBooking(null);
                }

                if (nextBooking != null) {
                    itemDto.setNextBooking(bookingMapper.toDto(nextBooking));
                } else {
                    itemDto.setNextBooking(null);
                }

                List<Comment> comments = commentRepository.findCommentsByItemId(itemDto.getId());
                List<CommentDto> commentsDto = new ArrayList<>();

                for (Comment comment : comments) {
                    CommentDto commentDto = commentMapper.toDto(comment);
                    commentDto.setAuthorName(comment.getAuthor().getName());
                    commentsDto.add(commentDto);
                }

                itemDto.setComments(commentsDto);

                allItemsDtoSortedById.add(itemDto);
            }
            return new ResponseEntity<>(allItemsDtoSortedById, HttpStatus.OK);
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь не найден"));
        }
    }

    public ResponseEntity<ItemDto> updateItem(Long userId, Long id, ItemCreationDto itemCreationDto) throws ValidationException {
        //       validateUpdateItem(userId, id, item);

        Optional<User> user = userRepository.findById(userId);
        Optional<Item> item = itemRepository.findById(id);

        if (user.isPresent() && item.isPresent()) {
            Item updateItem = new Item();
            updateItem.setId(id);
            updateItem.setUser(item.get().getUser());
            updateItem.setRequest(item.get().getRequest());

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
            return new ResponseEntity<>(itemMapper.toDto(itemFromRepository), HttpStatus.OK);
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь и/или вещь не найдены."));
        }
    }

    public ResponseEntity<List<ItemDto>> searchItem(Long userId, String text) {

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            String lowerCaseText = text.toLowerCase();
            List<ItemDto> itemsDto = new ArrayList<>();
            if (text.isBlank()) {
                return new ResponseEntity<>(itemsDto, HttpStatus.OK);
            } else {
                List<Optional<Item>> itemsFromRepository = itemRepository.searchItemsByText(text);
                for (Optional<Item> item : itemsFromRepository) {
                    if (item.get().getAvailable()) {
                        itemsDto.add(itemMapper.toDto(item));
                    }
                }
                return new ResponseEntity<>(itemsDto, HttpStatus.OK);
            }
        } else {
            throw new NotFoundException(String.format(
                    "Пользователь не найден"));
        }
    }

    public ResponseEntity<CommentDto> addComment(Long userId, Long itemId, CommentCreationDto commentCreationDto) {
        validateComment(userId, itemId, commentCreationDto);
        commentCreationDto.setItemId(itemId);
        commentCreationDto.setAuthor(userMapper.toUser(userRepository.findById(userId)));
        commentRepository.save(commentMapper.toComment(commentCreationDto));
        countComment++;
        CommentDto commentDto = commentMapper.toDto(commentRepository.findById(countComment));
        return new ResponseEntity<>(commentDto, HttpStatus.OK);
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
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            throw new NotFoundException(String.format(
                    "Хозяин вещи не найден"));
        }
    }

    public void validateComment(Long userId, Long itemId, CommentCreationDto commentCreationDto) throws ValidationException {
        if (commentCreationDto.getText() == null || commentCreationDto.getText().isBlank()) {
            log.info("Поле text отсутствует или пусто.");
            throw new ValidationException("Поле text отсутствует или пусто.");
        }

        Optional<User> user = userRepository.findById(userId);

        Optional<Item> item = itemRepository.findById(itemId);

        if (!user.isPresent()) {
            log.info("Пользователь не найден");
            throw new NotFoundException(String.format(
                    "Пользователь не найден"));
        }

        if (!item.isPresent()) {
            log.info("Вещь не найдена.");
            throw new NotFoundException(String.format(
                    "Вещь не найдена"));
        }

        List<Optional<Booking>> bookings = bookingRepository.findBookersByBookerId(userId);
        Booking booking = new Booking();
        boolean isUserEndUsedItem = false;

        for (Optional<Booking> bookingOptional : bookings) {
            Long itemIdFromBooking = bookingOptional.get().getItemId();
            if (itemIdFromBooking == itemId) {
                if (bookingOptional.get().getEnd().isBefore(LocalDateTime.now())) {
                    isUserEndUsedItem = true;
                    booking = bookingMapper.toBooking(bookingOptional);
                    break;
                }
            }
        }

        if (!isUserEndUsedItem) {
            log.info("Пользователь находится в процессе использования вещи и не может оставить комментарий." +
                    "Или пользователь не бронировал вещь и не может оставить комментарий.");
            throw new ValidationException("Пользователь находится в процессе использования вещи и не может оставить комментарий." +
                    "Или пользователь не бронировал вещь и не может оставить комментарий.");
        }
    }
}