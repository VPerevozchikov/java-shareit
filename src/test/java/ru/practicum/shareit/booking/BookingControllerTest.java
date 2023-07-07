package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    private User userOne;
    private User userTwo;
    private Item itemOne;
    private BookingCreationDto bookingCreationDtoOne;
    private Booking bookingOne;
    private ItemMapper itemMapper;
    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapperImpl();
        bookingMapper = new BookingMapperImpl();

        userOne = new User();
        userOne.setId(1L);
        userOne.setName("Vlad");
        userOne.setEmail("test@yandex.ru");

        userTwo = new User();
        userTwo.setId(2L);
        userTwo.setName("Ivan");
        userTwo.setEmail("ivan@yandex.ru");

        itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setUser(userOne);
        itemOne.setAvailable(true);
        itemOne.setDescription("1500Vt");
        itemOne.setName("Drill_1");


        bookingCreationDtoOne = new BookingCreationDto();
        bookingCreationDtoOne.setStatus(StatusType.WAITING);
        bookingCreationDtoOne.setItemId(1L);
        bookingCreationDtoOne.setBooker(userTwo);
        bookingCreationDtoOne.setStart(LocalDateTime.now().plusHours(10));
        bookingCreationDtoOne.setEnd(LocalDateTime.now().plusHours(100));

        bookingOne = bookingMapper.toBooking(bookingCreationDtoOne);
        bookingOne.setId(1L);
    }

    @SneakyThrows
    @Test
    void addBookingTest() {
        long userId = 1L;
        BookingDto bookingDto = bookingMapper.toDto(bookingOne, itemOne);

        when(bookingService.addBooking(any(), any()))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingCreationDtoOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).addBooking(any(), any());
        assertThat(objectMapper.writeValueAsString(bookingDto), equalTo(result));
    }

    @SneakyThrows
    @Test
    void approveBookingTest() {
        long userId = 1L;
        BookingDto bookingDto = bookingMapper.toDto(bookingOne, itemOne);

        when(bookingService.approveBooking(any(), any(), any()))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .queryParam("approved", "true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).approveBooking(any(), any(), any());
        assertThat(objectMapper.writeValueAsString(bookingDto), equalTo(result));
    }

    @SneakyThrows
    @Test
    void getBookingByIdTest() {
        long userId = 1L;
        BookingDto bookingDto = bookingMapper.toDto(bookingOne, itemOne);

        when(bookingService.approveBooking(any(), any(), any()))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .queryParam("approved", "true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).approveBooking(any(), any(), any());
        assertThat(objectMapper.writeValueAsString(bookingDto), equalTo(result));
    }

    @SneakyThrows
    @Test
    void getBookingByBookerIdTest() {
        long userId = 1L;
        BookingDto bookingDto = bookingMapper.toDto(bookingOne, itemOne);

        List<BookingDto> bookingsDto = new ArrayList<>();
        bookingsDto.add(bookingDto);

        when(bookingService.getBookingsByBookerId(any(), any(), any(), any()))
                .thenReturn(bookingsDto);

        String result = mockMvc.perform(get("/bookings")
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "20")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getBookingsByBookerId(any(), any(), any(), any());
        assertThat(objectMapper.writeValueAsString(bookingsDto), equalTo(result));
    }

    @SneakyThrows
    @Test
    void getBookingByOwnerIdTest() {
        long userId = 1L;
        BookingDto bookingDto = bookingMapper.toDto(bookingOne, itemOne);

        List<BookingDto> bookingsDto = new ArrayList<>();
        bookingsDto.add(bookingDto);

        when(bookingService.getBookingsByOwnerId(any(), any(), any(), any()))
                .thenReturn(bookingsDto);

        String result = mockMvc.perform(get("/bookings/owner")
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "20")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getBookingsByOwnerId(any(), any(), any(), any());
        assertThat(objectMapper.writeValueAsString(bookingsDto), equalTo(result));
    }

}
