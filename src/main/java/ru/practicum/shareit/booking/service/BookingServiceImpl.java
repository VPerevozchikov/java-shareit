package ru.practicum.shareit.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationBookingStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    ItemMapper itemMapper;
    UserMapper userMapper;
    BookingMapper bookingMapper;
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    Long count = 0L;

    public BookingServiceImpl(ItemRepository itemRepository,
                              UserRepository userRepository,
                              ItemMapper itemMapper,
                              UserMapper userMapper,
                              BookingRepository bookingRepository,
                              BookingMapper bookingMapper) {

        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    public ResponseEntity<BookingDto> addBooking(Long userId,
                                                 BookingCreationDto bookingCreationDto) throws ValidationException,
            NotFoundException {
        validateNewBooking(userId, bookingCreationDto);
        bookingCreationDto.setBooker(userMapper.toUser(userRepository.findById(userId)));
        if (bookingCreationDto.getStatus() == null) {
            bookingCreationDto.setStatus(StatusType.WAITING);
        }

        Booking newBooking = bookingMapper.toBooking(bookingCreationDto);

        bookingRepository.save(newBooking);
        count++;

        BookingDto bookingDto = bookingMapper.toDto(bookingRepository.findById(count));
        return new ResponseEntity<>(bookingDto, HttpStatus.CREATED);
    }

    public ResponseEntity<BookingDto> approveBooking(Long userId, Long bookingId, String approved) throws NotFoundException,
            ValidationException {
        validateApproved(userId, bookingId, approved);

        Optional<Booking> bookingFromOptional = bookingRepository.findById(bookingId);

        Booking booking = bookingMapper.toBooking(bookingFromOptional);

        if (approved.equals("true")) {
            booking.setStatus(StatusType.APPROVED);
        } else {
            booking.setStatus(StatusType.REJECTED);
        }

        bookingRepository.save(booking);

        BookingDto bookingDto = bookingMapper.toDto(bookingRepository.findById(bookingId));
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    public ResponseEntity<BookingDto> getBookingById(Long userId, Long bookingId) throws NotFoundException {
        validateRequest(userId, bookingId);

        BookingDto bookingDto = bookingMapper.toDto(bookingRepository.findById(bookingId));
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    public ResponseEntity<List<BookingDto>> getBookingsByBookerId(Long bookerId, String state) throws ValidationException,
            ValidationBookingStatusException {
        validateRequestBookingsByBookerOrOwnerId(bookerId);
        validateState(state);
        List<BookingDto> bookingsDto = new ArrayList<>();

        if (state.equals("ALL")) {
            List<Optional<Booking>> bookings = bookingRepository.findBookersByBookerId(bookerId);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking));
            }
        }

        if (state.equals("WAITING") || state.equals("REJECTED")) {
            List<Optional<Booking>> bookings = bookingRepository.findBookersByBookerIdAndTypicalStatus(bookerId, state);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking));
            }
        }

        if (state.equals("FUTURE")) {
            LocalDateTime localDateTime = LocalDateTime.now();
            List<Optional<Booking>> bookings = bookingRepository.findFutureBookersByBookerId(bookerId, localDateTime);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking));
            }
        }

        if (state.equals("PAST")) {
            LocalDateTime localDateTime = LocalDateTime.now();
            List<Optional<Booking>> bookings = bookingRepository.findPastBookersByBookerId(bookerId, localDateTime);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking));
            }
        }

        if (state.equals("CURRENT")) {
            LocalDateTime localDateTime = LocalDateTime.now();
            List<Optional<Booking>> bookings = bookingRepository.findCurrentBookersByBookerId(bookerId, localDateTime);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking));
            }
        }

        return new ResponseEntity<>(bookingsDto, HttpStatus.OK);
    }

    public ResponseEntity<List<BookingDto>> getBookingsByOwnerId(Long ownerId, String state) throws ValidationException,
            ValidationBookingStatusException {
        validateRequestBookingsByBookerOrOwnerId(ownerId);
        validateState(state);
        List<BookingDto> bookingsDto = new ArrayList<>();

        if (state.equals("ALL")) {
            List<Optional<Booking>> bookings = bookingRepository.findBookingsByOwnerId(ownerId);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking));
            }
        }

        if (state.equals("WAITING") || state.equals("REJECTED")) {
            List<Optional<Booking>> bookings = bookingRepository.findBookersByOwnerIdAndTypicalStatus(ownerId, state);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking));
            }
        }

        if (state.equals("FUTURE")) {
            LocalDateTime localDateTime = LocalDateTime.now();
            List<Optional<Booking>> bookings = bookingRepository.findFutureBookersByOwnerId(ownerId, localDateTime);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking));
            }
        }

        if (state.equals("PAST")) {
            LocalDateTime localDateTime = LocalDateTime.now();
            List<Optional<Booking>> bookings = bookingRepository.findPastBookersByOwnerId(ownerId, localDateTime);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking));
            }
        }

        if (state.equals("CURRENT")) {
            LocalDateTime localDateTime = LocalDateTime.now();
            List<Optional<Booking>> bookings = bookingRepository.findCurrentBookersByOwnerId(ownerId, localDateTime);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking));
            }
        }

        return new ResponseEntity<>(bookingsDto, HttpStatus.OK);
    }


    public void validateNewBooking(Long userId, BookingCreationDto bookingCreationDto) throws ValidationException,
            NotFoundException {

        Optional<User> user = userRepository.findById(userId);

        Optional<Item> item = itemRepository.findById(bookingCreationDto.getItemId());

        if (!user.isPresent()) {
            log.info("Валидация новой брони. Хозяин вещи не найден");
            throw new NotFoundException(String.format(
                    "Хозяин вещи не найден"));
        }

        if (!item.isPresent()) {
            log.info("Валидация новой брони. Вещь не найдена.");
            throw new NotFoundException(String.format(
                    "Вещь не найдена"));
        }

        if (!item.get().getAvailable()) {
            log.info("Валидация новой брони. Бронь вещи недоступна");
            throw new ValidationException(String.format(
                    "Бронь вещи недоступна"));
        }

        if (bookingCreationDto.getStart() == null || bookingCreationDto.getEnd() == null) {
            log.info("Валидация новой брони. Время старта и/или окончания бронирования не задано");
            throw new ValidationException(String.format(
                    "Время старта и/или окончания бронирования не задано"));
        }


        if (bookingCreationDto.getStart().isBefore(LocalDateTime.now().minusSeconds(15))) {
            log.info("Валидация новой брони. Время окончания и/или старта в прошлом");
            throw new ValidationException(String.format(
                    "Время окончания и/или старта в прошлом"));
        }

        if (bookingCreationDto.getEnd().isBefore(bookingCreationDto.getStart())) {
            log.info("Валидация новой брони. Значение даты окончания задано как более раннее в сравнение с датой старта");
            throw new ValidationException(String.format(
                    "Значение даты окончания задано как более раннее в сравнение с датой старта"));
        }

        if (bookingCreationDto.getEnd().isEqual(bookingCreationDto.getStart())) {
            log.info("Валидация новой брони. Значение даты окончания равно дате старта");
            throw new ValidationException(String.format(
                    "Значение даты окончания равно дате старта"));
        }

        if (userId == item.get().getUser().getId()) {
            log.info("Валидация новой брони. Попытка создань бронь владельца вещи на собственную вещь");
            throw new NotFoundException(String.format(
                    "Попытка создань бронь владельца вещи на собственную вещь"));
        }
    }

    public void validateApproved(Long userId, Long bookingId, String approved) throws NotFoundException,
            ValidationException {

        Optional<User> user = userRepository.findById(userId);

        Optional<Booking> booking = bookingRepository.findById(bookingId);
        Optional<Item> item = itemRepository.findById(booking.get().getItemId());


        if (!user.isPresent() || !booking.isPresent()) {
            throw new NotFoundException(String.format(
                    "Хозяин вещи и/или вещь не найдены"));
        }

        Long ownerId = item.get().getUser().getId();

        if (!userId.equals(ownerId)) {
            throw new NotFoundException(String.format(
                    "Хозяин вещи не соотвествует брони"));
        }

        String currentStatus = String.valueOf(booking.get().getStatus());

        if (currentStatus.equals("APPROVED") && approved.equals("true")) {
            throw new ValidationException(String.format(
                    "Попытка повторно одобрить бронь."));
        }

    }

    public void validateRequest(Long userId, Long bookingId) throws NotFoundException {

        Optional<User> user = userRepository.findById(userId);

        Optional<Booking> booking = bookingRepository.findById(bookingId);


        if (!user.isPresent() || !booking.isPresent()) {
            throw new NotFoundException(String.format(
                    "Хозяин вещи и/или вещь не найдены"));
        }

        Optional<Item> item = itemRepository.findById(booking.get().getItemId());
        Long ownerBookingId = booking.get().getBooker().getId();
        Long ownerItemId = item.get().getUser().getId();

        if (!userId.equals(ownerBookingId) && !userId.equals(ownerItemId)) {
            throw new NotFoundException(String.format(
                    "Хозяин вещи не соотвествует брони"));
        }
    }

    public void validateRequestBookingsByBookerOrOwnerId(Long bookerId) throws ValidationException {

        Optional<User> user = userRepository.findById(bookerId);

        if (!user.isPresent()) {
            throw new NotFoundException(String.format(
                    "Хозяин вещи/пользователь не найден."));
        }
    }

    public void validateState(String state) throws ValidationBookingStatusException {
        if (!state.equals("ALL")
                && !state.equals("CURRENT")
                && !state.equals("PAST")
                && !state.equals("FUTURE")
                && !state.equals("WAITING")
                && !state.equals("REJECTED")) {
            throw new ValidationBookingStatusException(String.format(
                    "Unknown state: UNSUPPORTED_STATUS"));
        }
    }
}
