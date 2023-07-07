package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    private ItemMapper itemMapper;
    private ItemRequestMapper itemRequestMapper;
    private ItemRequestCreationDto itemRequestCreationDtoOne;
    private ItemRequest itemRequestOne;
    private User userOne;
    private Item itemOne;
    private List<ItemDto> items;

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapperImpl();
        itemRequestMapper = new ItemRequestMapperImpl();

        itemRequestCreationDtoOne = new ItemRequestCreationDto();
        itemRequestCreationDtoOne.setDescription("Drill strong");

        itemRequestOne = new ItemRequest();
        itemRequestOne.setId(1L);
        itemRequestOne.setRequestor(1L);
        itemRequestOne.setCreated(LocalDateTime.now());
        itemRequestOne.setDescription(itemRequestCreationDtoOne.getDescription());

        userOne = new User();
        userOne.setId(1L);
        userOne.setName("Vlad");
        userOne.setEmail("test@yandex.ru");

        itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setUser(userOne);
        itemOne.setDescription("1500Vt");
        itemOne.setAvailable(true);
        itemOne.setName("Drill");
        itemOne.setRequestId(1L);

        items = new ArrayList<>();
        items.add(itemMapper.toDto(itemOne));
    }

    @SneakyThrows
    @Test
    void addItemRequestTest() {
        long userId = 1L;
        ItemRequestDto itemRequestDto = itemRequestMapper.toDto(Optional.of(itemRequestOne), items);

        when(itemRequestService.addItemRequest(any(), any()))
                .thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests", itemRequestCreationDtoOne)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestCreationDtoOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).addItemRequest(any(), any());
        assertThat(objectMapper.writeValueAsString(itemRequestDto), equalTo(result));
    }

    @SneakyThrows
    @Test
    void getItemRequestByUserIdTest() {
        long userId = 1L;
        ItemRequestDto itemRequestDto = itemRequestMapper.toDto(Optional.of(itemRequestOne), items);
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        itemRequestsDto.add(itemRequestDto);

        when(itemRequestService.getItemRequestsByUserId(any()))
                .thenReturn(itemRequestsDto);

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getItemRequestsByUserId(any());
        assertThat(objectMapper.writeValueAsString(itemRequestsDto), equalTo(result));
    }

    @SneakyThrows
    @Test
    void getItemRequestByAnotherUsersTest() {
        long userId = 1L;
        ItemRequestDto itemRequestDto = itemRequestMapper.toDto(Optional.of(itemRequestOne), items);
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        itemRequestsDto.add(itemRequestDto);

        when(itemRequestService.getRequestsByAnotherUsers(any(), any(), any()))
                .thenReturn(itemRequestsDto);

        String result = mockMvc.perform(get("/requests/all")
                        .queryParam("from", "0")
                        .queryParam("size", "20")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getRequestsByAnotherUsers(any(), any(), any());
        assertThat(objectMapper.writeValueAsString(itemRequestsDto), equalTo(result));
    }

    @SneakyThrows
    @Test
    void getItemRequestByIdTest() {
        long userId = 1L;
        ItemRequestDto itemRequestDto = itemRequestMapper.toDto(Optional.of(itemRequestOne), items);

        when(itemRequestService.getItemRequestById(any(), any()))
                .thenReturn(itemRequestDto);

        String result = mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getItemRequestById(any(), any());
        assertThat(objectMapper.writeValueAsString(itemRequestDto), equalTo(result));
    }
}
