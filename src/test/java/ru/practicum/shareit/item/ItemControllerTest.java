package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private ItemMapper itemMapper;
    private CommentMapper commentMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    private User userOne;
    private ItemCreationDto itemCreationDtoOne;
    private Item itemOne;
    private CommentCreationDto commentCreationDto;
    private Comment commentOne;


    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapperImpl();
        commentMapper = new CommentMapper();

        userOne = new User();
        userOne.setId(1L);
        userOne.setName("Vlad");
        userOne.setEmail("test@yandex.ru");

        itemCreationDtoOne = new ItemCreationDto();
        itemCreationDtoOne.setUser(userOne);
        itemCreationDtoOne.setDescription("1500Vt");
        itemCreationDtoOne.setAvailable(true);
        itemCreationDtoOne.setName("Drill");

        itemOne = itemMapper.toItem(itemCreationDtoOne);
        itemOne.setId(1L);

        commentCreationDto = new CommentCreationDto();
        commentCreationDto.setAuthor(userOne);
        commentCreationDto.setItemId(1L);
        commentCreationDto.setText("Good thing");

        commentOne = new Comment();
        commentOne.setId(1L);
        commentOne.setAuthor(userOne);
        commentOne.setItemId(1L);
        commentOne.setText("Good thing");
        commentOne.setCreated(LocalDateTime.of(2023, 10, 10, 10, 10, 10));

    }


    @SneakyThrows
    @Test
    void addItemTest() {
        long userId = 1L;
        ItemDto itemDto = itemMapper.toDto(itemOne);

        when(itemService.addItem(any(), any()))
                .thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemCreationDtoOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).addItem(any(), any());
        assertThat(objectMapper.writeValueAsString(itemDto), equalTo(result));
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        long userId = 1L;
        ItemDto itemDto = itemMapper.toDto(itemOne);

        when(itemService.updateItem(any(), any(), any()))
                .thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{id}", 1L, itemCreationDtoOne)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemCreationDtoOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).updateItem(any(), any(), any());
        assertThat(objectMapper.writeValueAsString(itemDto), equalTo(result));
    }

    @SneakyThrows
    @Test
    void deleteItemTest() {
        long userId = 1L;
        ItemDto itemDto = itemMapper.toDto(itemOne);

        when(itemService.updateItem(any(), any(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(delete("/items/{id}", 1L))
                .andExpect(status().isOk());


        verify(itemService).deleteItem(any());
    }

    @SneakyThrows
    @Test
    void getItemByIdTest() {

        when(itemService.getItemById(any(), any()))
                .thenReturn(itemMapper.toDto(itemOne));

        mockMvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemOne.getName())));
        verify(itemService).getItemById(any(), any());
    }

    @SneakyThrows
    @Test
    void getItemsByUserIdTest() {
        ItemDto itemDto = itemMapper.toDto(itemOne);
        Set<ItemDto> itemsDto = new HashSet<>();
        itemsDto.add(itemDto);

        when(itemService.getItemsByUserId(any()))
                .thenReturn(itemsDto);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).getItemsByUserId(any());
        assertThat(objectMapper.writeValueAsString(itemsDto), equalTo(result));
    }

    @SneakyThrows
    @Test
    void searchItemTest() {
        ItemDto itemDto = itemMapper.toDto(itemOne);
        List<ItemDto> itemsDto = new ArrayList<>();
        itemsDto.add(itemDto);

        when(itemService.searchItem(any(), any()))
                .thenReturn(itemsDto);

        String result = mockMvc.perform(get("/items/search")
                        .queryParam("text", "search")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).searchItem(any(), any());
        assertThat(objectMapper.writeValueAsString(itemsDto), equalTo(result));
    }

    @SneakyThrows
    @Test
    void addCommentTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setAuthor(userOne);
        commentDto.setItemId(1L);
        commentDto.setText("Good thing");
        commentDto.setCreated(LocalDateTime.of(2023, 10, 10, 10, 10, 10));

        when(itemService.addComment(any(), any(), any()))
                .thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", 1L, commentCreationDto)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentCreationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).addComment(any(), any(), any());
        assertThat(objectMapper.writeValueAsString(commentDto), equalTo(result));
    }

}
