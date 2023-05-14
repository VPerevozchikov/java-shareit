package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repositories.ItemRepository;

import java.util.Optional;

@Component
public class BookingMapperImpl implements BookingMapper {

    ItemRepository itemRepository;
    ItemMapper itemMapper;

    public BookingMapperImpl(ItemRepository itemRepository,
                             ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    public BookingDto toDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(itemMapper.toItem(itemRepository.findById(booking.getItemId())));
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBookerId(booking.getBooker().getId());
        return bookingDto;
    }

    public BookingDto toDto(Optional<Booking> booking) {
        if (booking.isPresent()) {
            BookingDto bookingDto = new BookingDto();
            bookingDto.setId(booking.get().getId());
            bookingDto.setStart(booking.get().getStart());
            bookingDto.setEnd(booking.get().getEnd());
//            bookingDto.setItemId(booking.get().getItemId());
            bookingDto.setItem(itemMapper.toItem(itemRepository.findById(booking.get().getItemId())));
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
