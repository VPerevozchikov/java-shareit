package ru.practicum.shareit.booking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.repositories.UserRepository;

import javax.validation.ValidationException;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    BookingService bookingService;

    public BookingController(ItemRepository itemRepository,
                             UserRepository userRepository,
                             BookingRepository bookingRepository,
                             BookingService bookingService) {

        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestBody BookingCreationDto bookingCreationDto) throws ValidationException {
        log.info("Запрос на создание брони.");
        return bookingService.addBooking(userId, bookingCreationDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long bookingId,
                                                     @RequestParam(value = "approved", required = true) String approved) throws ValidationException {
        log.info("Запрос на подтверждение брони.");
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long bookingId) {
        log.info("Запрос на получение информации о брони.");
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                                  @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Запрос на получение информации о бронях по пользователю.");
        return bookingService.getBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getBookingByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                                @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Запрос на получение информации о бронях по хозяину вещи.");
        return bookingService.getBookingsByOwnerId(ownerId, state);
    }
}
