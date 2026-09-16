package ru.practicum.shareit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//DTO для создания запроса вещи
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    @NotBlank
    @Size(max = 1000)
    private String description;
}