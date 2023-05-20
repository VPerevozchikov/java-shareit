package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

@Component
public class BookingMapperImpl implements BookingMapper {

    public BookingDto toDto(Booking booking, Item item) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(item);
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBookerId(booking.getBooker().getId());
        return bookingDto;
    }

    public BookingDto toDto(Optional<Booking> booking, Item item) {
        if (booking.isPresent()) {
            BookingDto bookingDto = new BookingDto();
            bookingDto.setId(booking.get().getId());
            bookingDto.setStart(booking.get().getStart());
            bookingDto.setEnd(booking.get().getEnd());
            bookingDto.setItem(item);
            bookingDto.setBooker(booking.get().getBooker());
            bookingDto.setStatus(booking.get().getStatus());
            bookingDto.setBookerId(booking.get().getBooker().getId());
            return bookingDto;
        } else {
            throw new NotFoundException(String.format(
                    "Бронь не найдена"));
        }
    }

    public Booking toBooking(BookingCreationDto bookingCreationDto) {
        Booking booking = new Booking();
        booking.setStart(bookingCreationDto.getStart());
        booking.setEnd(bookingCreationDto.getEnd());
        booking.setItemId(bookingCreationDto.getItemId());
        booking.setBooker(bookingCreationDto.getBooker());
        booking.setStatus(bookingCreationDto.getStatus());
        return booking;
    }

    public Booking toBooking(Optional<Booking> bookingFromOptional) {
        Booking booking = new Booking();
        booking.setId(bookingFromOptional.get().getId());
        booking.setStart(bookingFromOptional.get().getStart());
        booking.setEnd(bookingFromOptional.get().getEnd());
        booking.setItemId(bookingFromOptional.get().getItemId());
        booking.setBooker(bookingFromOptional.get().getBooker());
        booking.setStatus(bookingFromOptional.get().getStatus());
        return booking;
    }
}
