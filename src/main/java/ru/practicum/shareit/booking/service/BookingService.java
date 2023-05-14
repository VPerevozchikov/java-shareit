package ru.practicum.shareit.booking.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;

public interface BookingService {
    ResponseEntity<BookingDto> addBooking(Long userId,
                                          BookingCreationDto bookingCreationDto) throws ValidationException,
            NotFoundException;

    ResponseEntity<BookingDto> approveBooking(Long userId,
                                              Long bookingId,
                                              String approved) throws NotFoundException,
            ValidationException;

    ResponseEntity<BookingDto> getBookingById(Long userId,
                                              Long bookingId) throws NotFoundException;

    ResponseEntity<List<BookingDto>> getBookingsByBookerId(Long bookerId, String state) throws ValidationException;

    ResponseEntity<List<BookingDto>> getBookingsByOwnerId(Long ownerId, String state) throws ValidationException;
}