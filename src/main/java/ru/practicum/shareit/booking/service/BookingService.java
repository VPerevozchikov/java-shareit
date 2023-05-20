package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId,
                          BookingCreationDto bookingCreationDto) throws ValidationException,
            NotFoundException;

    BookingDto approveBooking(Long userId,
                              Long bookingId,
                              String approved) throws NotFoundException,
            ValidationException;

    BookingDto getBookingById(Long userId,
                              Long bookingId) throws NotFoundException;

    List<BookingDto> getBookingsByBookerId(Long bookerId, String state) throws ValidationException;

    List<BookingDto> getBookingsByOwnerId(Long ownerId, String state) throws ValidationException;

    List<Booking> getBookingsByItemId(Long itemId);
}