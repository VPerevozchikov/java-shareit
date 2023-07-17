package ru.practicum.shareit.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationBookingStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class BookingServiceImpl implements BookingService {
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private Long count = 0L;

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
    }

    @Override
    public BookingDto addBooking(Long userId,
                                 BookingCreationDto bookingCreationDto) {

        validateNewBooking(userId, bookingCreationDto);
        bookingCreationDto.setBooker(userService.getUserById(userId));
        if (bookingCreationDto.getStatus() == null) {
            bookingCreationDto.setStatus(StatusType.WAITING);
        }

        Booking newBooking = bookingMapper.toBooking(bookingCreationDto);

        bookingRepository.save(newBooking);
        count++;

        BookingDto bookingDto = bookingMapper.toDto(
                bookingRepository.findById(count),
                itemMapper.toItem(itemRepository.findById(bookingCreationDto.getItemId())));
        return bookingDto;
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, String approved) {
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
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long userId, Long bookingId) {
        validateRequest(userId, bookingId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        Optional<Item> item = itemRepository.findById(booking.get().getItemId());

        BookingDto bookingDto = bookingMapper.toDto(booking,
                itemMapper.toItem(item));
        return bookingDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByBookerId(Long bookerId,
                                                  String state,
                                                  Integer from,
                                                  Integer size) {
        validateRequestBookingsByBookerOrOwnerId(bookerId);
        validateState(state);
        validatePageQuery(from, size);
        List<BookingDto> bookingsDto = new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        if (state.equals("ALL")) {
            Page<Booking> bookings = bookingRepository.findBookersByBookerId(bookerId, pageRequest);

            bookings.forEach((booking) -> bookingsDto.add(bookingMapper.toDto(booking,
                    itemMapper.toItem(itemRepository.findById(booking.getItemId())))));
        }

        if (state.equals("WAITING")) {
            List<Optional<Booking>> bookings = bookingRepository.findBookersByBookerIdAndTypicalStatus(bookerId, state);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.get().getItemId()))));
            }
        }

        if (state.equals("REJECTED")) {
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
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByOwnerId(Long ownerId,
                                                 String state,
                                                 Integer from,
                                                 Integer size) {
        validateRequestBookingsByBookerOrOwnerId(ownerId);
        validateState(state);
        validatePageQuery(from, size);
        List<BookingDto> bookingsDto = new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        if (state.equals("ALL")) {
            Page<Booking> bookings = bookingRepository.findBookingsByOwnerId(ownerId, pageRequest);
            for (Booking booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.getItemId()))));
            }
        }

        if (state.equals("WAITING")) {
            List<Optional<Booking>> bookings = bookingRepository.findBookersByOwnerIdAndTypicalStatus(ownerId, state);
            for (Optional<Booking> booking : bookings) {
                bookingsDto.add(bookingMapper.toDto(booking,
                        itemMapper.toItem(itemRepository.findById(booking.get().getItemId()))));
            }
        }

        if (state.equals("REJECTED")) {
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
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByItemId(Long itemId) {
        List<Optional<Booking>> listOfBookingsByItemId = bookingRepository.findBookingByItemIdOrderByStartDesc(itemId);
        List<Booking> listOfBookings = new ArrayList<>();
        for (Optional<Booking> booking : listOfBookingsByItemId) {
            listOfBookings.add(bookingMapper.toBooking(booking));
        }
        return listOfBookings;
    }

    public void validateNewBooking(Long userId, BookingCreationDto bookingCreationDto) {

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

    public void validateApproved(Long userId, Long bookingId, String approved) {

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

    public void validateRequest(Long userId, Long bookingId) {

        User user = userService.getUserById(userId);

        Optional<Booking> booking = bookingRepository.findById(bookingId);


        if (!booking.isPresent()) {
            throw new NotFoundException(String.format(
                    "Бронь не найдена."));
        }

        Optional<Item> item = itemRepository.findById(booking.get().getItemId());
        Long ownerBookingId = booking.get().getBooker().getId();
        Long ownerItemId = item.get().getUser().getId();

        if (!userId.equals(ownerBookingId) && !userId.equals(ownerItemId)) {
            throw new NotFoundException(String.format(
                    "Хозяин вещи не соотвествует брони"));
        }
    }

    public void validateRequestBookingsByBookerOrOwnerId(Long bookerId) {
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

    public void validatePageQuery(Integer from, Integer size) {
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
}
