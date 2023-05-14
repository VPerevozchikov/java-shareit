package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CommentMapperImpl implements CommentMapper {

    public Comment toComment(CommentCreationDto commentCreationDto) {
        Comment comment = new Comment();
        comment.setText(commentCreationDto.getText());
        comment.setAuthor(commentCreationDto.getAuthor());
        comment.setItemId(commentCreationDto.getItemId());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthor(comment.getAuthor());
        commentDto.setItemId(comment.getItemId());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public CommentDto toDto(Optional<Comment> comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.get().getId());
        commentDto.setText(comment.get().getText());
        commentDto.setAuthor(comment.get().getAuthor());
        commentDto.setItemId(comment.get().getItemId());
        commentDto.setAuthorName(comment.get().getAuthor().getName());
        commentDto.setCreated(comment.get().getCreated());
        return commentDto;

    }

    public Comment toComment(Optional<Comment> commentOptional) {
        Comment comment = new Comment();
        comment.setId(commentOptional.get().getId());
        comment.setText(commentOptional.get().getText());
        comment.setAuthor(commentOptional.get().getAuthor());
        comment.setItemId(commentOptional.get().getItemId());
        comment.setCreated(commentOptional.get().getCreated());
        return comment;
    }
}
