package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//DTO для обновления вещи
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemRequest {

    @Pattern(regexp = ".*\\S.*")
    private String name;

    @Pattern(regexp = ".*\\S.*")
    private String description;

    private Boolean available;
}