package ru.practicum.shareit.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//DTO для создания комментария
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentRequest {

    @NotBlank
    @Size(max = 2000)
    private String text;
}