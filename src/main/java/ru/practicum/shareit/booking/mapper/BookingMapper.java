package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

public interface BookingMapper {
    BookingDto toDto(Booking booking);

    BookingDto toDto(Optional<Booking> booking);

    Booking toBooking(BookingCreationDto bookingCreationDto);

    Booking toBooking(Optional<Booking> booking);

}
