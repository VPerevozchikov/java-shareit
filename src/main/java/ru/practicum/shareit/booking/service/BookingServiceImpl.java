package ru.practicum.shareit.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    //  ItemService itemService;
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    ItemRepository itemRepository;
    BookingRepository bookingRepository;
    ItemMapper itemMapper;
    BookingMapper bookingMapper;
    UserService userService;
    Long count = 0L;

    public BookingServiceImpl(ItemRepository itemRepository,
                              ItemMapper itemMapper,
                              BookingRepository bookingRepository,
                              BookingMapper bookingMapper,
                              UserService userService) {

        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
        //    this.itemService = itemService;
    }

    @Override
    public BookingDto addBooking(Long userId,
                                 BookingCreationDto bookingCreationDto) throws ValidationException,
            NotFoundException {

        validateNewBooking(userId, bookingCreationDto);
        bookingCreationDto.setBooker(userService.getUserById(userId));
        if (bookingCreationDto.getStatus() == null) {
            bookingCreationDto.setStatus(StatusType.WAITING);
        }

        Booking newBooking = bookingMapper.toBooking(bookingCreationDto);

        bookingRepository.save(newBooking);
        count++;

        BookingDto bookingDto = bookingMapper.toDto(bookingRepository.findById(count),
                itemMapper.toItem(itemRepository.findById(bookingCreationDto.getItemId())));
        return bookingDto;
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, String approved) throws NotFoundException,
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

        BookingDto bookingDto = bookingMapper.toDto(bookingRepository.findById(bookingId),
                itemMapper.toItem(itemRepository.findById(booking.getItemId())));
        return bookingDto;
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) throws NotFoundException {
        validateRequest(userId, bookingId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        BookingDto bookingDto = bookingMapper.toDto(booking,
                itemMapper.toItem(itemRepository.findById(booking.get().getItemId())));
        return bookingDto;
    }

    @Override
    public List<BookingDto> getBookingsByBookerId(Long bookerId, String state) throws ValidationException,
            ValidationBookingStatusException {
        validateRequestBookingsByBookerOrOwnerId(bookerId);
        validateState(state);
        List<BookingDto> bookingsDto = new ArrayList<>();

        if (state.equals("ALL")) {
            List<Optional<Booking>> bookings = bookingRepository.findBookersByBookerId(bookerId);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.get().getItemId()))));
            }
        }

        if (state.equals("WAITING") || state.equals("REJECTED")) {
            List<Optional<Booking>> bookings = bookingRepository.findBookersByBookerIdAndTypicalStatus(bookerId, state);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.get().getItemId()))));
            }
        }

        if (state.equals("FUTURE")) {
            LocalDateTime localDateTime = LocalDateTime.now();
            List<Optional<Booking>> bookings = bookingRepository.findFutureBookersByBookerId(bookerId, localDateTime);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.get().getItemId()))));
            }
        }

        if (state.equals("PAST")) {
            LocalDateTime localDateTime = LocalDateTime.now();
            List<Optional<Booking>> bookings = bookingRepository.findPastBookersByBookerId(bookerId, localDateTime);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.get().getItemId()))));
            }
        }

        if (state.equals("CURRENT")) {
            LocalDateTime localDateTime = LocalDateTime.now();
            List<Optional<Booking>> bookings = bookingRepository.findCurrentBookersByBookerId(bookerId, localDateTime);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.get().getItemId()))));
            }
        }

        return bookingsDto;
    }

    @Override
    public List<BookingDto> getBookingsByOwnerId(Long ownerId, String state) throws ValidationException,
            ValidationBookingStatusException {
        validateRequestBookingsByBookerOrOwnerId(ownerId);
        validateState(state);
        List<BookingDto> bookingsDto = new ArrayList<>();

        if (state.equals("ALL")) {
            List<Optional<Booking>> bookings = bookingRepository.findBookingsByOwnerId(ownerId);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.get().getItemId()))));
            }
        }

        if (state.equals("WAITING") || state.equals("REJECTED")) {
            List<Optional<Booking>> bookings = bookingRepository.findBookersByOwnerIdAndTypicalStatus(ownerId, state);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.get().getItemId()))));
            }
        }

        if (state.equals("FUTURE")) {
            LocalDateTime localDateTime = LocalDateTime.now();
            List<Optional<Booking>> bookings = bookingRepository.findFutureBookersByOwnerId(ownerId, localDateTime);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.get().getItemId()))));
            }
        }

        if (state.equals("PAST")) {
            LocalDateTime localDateTime = LocalDateTime.now();
            List<Optional<Booking>> bookings = bookingRepository.findPastBookersByOwnerId(ownerId, localDateTime);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.get().getItemId()))));
            }
        }

        if (state.equals("CURRENT")) {
            LocalDateTime localDateTime = LocalDateTime.now();
            List<Optional<Booking>> bookings = bookingRepository.findCurrentBookersByOwnerId(ownerId, localDateTime);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.get().getItemId()))));
            }
        }

        return bookingsDto;
    }

    @Override
    public List<Booking> getBookingsByItemId(Long itemId) {
        List<Optional<Booking>> listOfBookingsByItemId = bookingRepository.findBookingByItemIdOrderByStartDesc(itemId);
        List<Booking> listOfBookings = new ArrayList<>();
        for (Optional<Booking> booking : listOfBookingsByItemId) {
            listOfBookings.add(bookingMapper.toBooking(booking));
        }
        return listOfBookings;
    }

    public void validateNewBooking(Long userId, BookingCreationDto bookingCreationDto) throws ValidationException,
            NotFoundException {

        User user = userService.getUserById(userId);

        Optional<Item> item = itemRepository.findById(bookingCreationDto.getItemId());


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

        if (userId.equals(item.get().getUser().getId())) {
            log.info("Валидация новой брони. Попытка создань бронь владельца вещи на собственную вещь");
            throw new NotFoundException(String.format(
                    "Попытка создань бронь владельца вещи на собственную вещь"));
        }
    }

    public void validateApproved(Long userId, Long bookingId, String approved) throws NotFoundException,
            ValidationException {

        User user = userService.getUserById(userId);

        Optional<Booking> booking = bookingRepository.findById(bookingId);
        Optional<Item> item = itemRepository.findById(booking.get().getItemId());


        if (!booking.isPresent()) {
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

        User user = userService.getUserById(userId);

        Optional<Booking> booking = bookingRepository.findById(bookingId);


        if (!booking.isPresent()) {
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
        User user = userService.getUserById(bookerId);
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
