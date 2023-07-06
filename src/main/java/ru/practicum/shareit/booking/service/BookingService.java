package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId,
                          BookingCreationDto bookingCreationDto);

    BookingDto approveBooking(Long userId,
                              Long bookingId,
                              String approved);

    BookingDto getBookingById(Long userId,
                              Long bookingId);

    List<BookingDto> getBookingsByBookerId(Long bookerId,
                                           String state,
                                           Integer from,
                                           Integer size);

    List<BookingDto> getBookingsByOwnerId(Long ownerId,
                                          String state,
                                          Integer from,
                                          Integer size);

    List<Booking> getBookingsByItemId(Long itemId);
}