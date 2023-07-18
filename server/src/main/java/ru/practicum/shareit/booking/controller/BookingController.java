package ru.practicum.shareit.booking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.ItemController;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {

        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestBody BookingCreationDto bookingCreationDto) {
        log.info("Запрос на создание брони.");
        return new ResponseEntity<>(bookingService.addBooking(userId, bookingCreationDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long bookingId,
                                                     @RequestParam(value = "approved",
                                                             required = true) String approved) {
        log.info("Запрос на подтверждение брони.");
        return new ResponseEntity<>(bookingService.approveBooking(userId, bookingId, approved), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long bookingId) {
        log.info("Запрос на получение информации о брони.");
        return new ResponseEntity<>(bookingService.getBookingById(userId, bookingId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                                  @RequestParam(value = "state",
                                                                          required = false,
                                                                          defaultValue = "ALL") String state,
                                                                  @RequestParam(value = "from",
                                                                          required = false,
                                                                          defaultValue = "0") Integer from,
                                                                  @RequestParam(value = "size",
                                                                          required = false,
                                                                          defaultValue = "20") Integer size) {
        log.info("Запрос на получение информации о бронях по пользователю.");
        return new ResponseEntity<>(bookingService.getBookingsByBookerId(bookerId, state, from, size), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getBookingByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                                @RequestParam(value = "state",
                                                                        required = false,
                                                                        defaultValue = "ALL") String state,
                                                                @RequestParam(value = "from",
                                                                        required = false,
                                                                        defaultValue = "0") Integer from,
                                                                @RequestParam(value = "size",
                                                                        required = false,
                                                                        defaultValue = "20") Integer size) {
        log.info("Запрос на получение информации о бронях по хозяину вещи.");
        return new ResponseEntity<>(bookingService.getBookingsByOwnerId(ownerId, state, from, size), HttpStatus.OK);
    }
}