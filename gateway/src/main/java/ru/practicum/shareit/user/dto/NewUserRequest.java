package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//DTO для создания нового пользователя
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
}