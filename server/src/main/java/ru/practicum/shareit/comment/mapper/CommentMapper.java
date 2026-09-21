package ru.practicum.shareit.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

//Маппер для преобразования комментария в DTO
@UtilityClass
public class CommentMapper {

    //Преобразование Comment в CommentDto
    public CommentDto toCommentDto(Comment comment) {
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
}