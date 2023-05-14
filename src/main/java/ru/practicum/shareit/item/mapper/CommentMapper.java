package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.Optional;

public interface CommentMapper {
    CommentDto toDto(Comment comment);

    Comment toComment(CommentCreationDto commentCreationDto);

    CommentDto toDto(Optional<Comment> comment);

    Comment toComment(Optional<Comment> comment);
}
