package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

//Маппер для преобразования комментария в DTO
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        String authorName = null;

        if (comment.getAuthor() != null) {
            authorName = comment.getAuthor().getName();
        }

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(authorName)
                .created(comment.getCreated())
                .build();
    }

    private CommentMapper() {
    }
}