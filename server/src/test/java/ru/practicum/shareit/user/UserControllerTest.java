package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Тесты контроллера пользователей
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    //Создание пользователя
    @Test
    void createShouldReturnCreatedUser() throws Exception {
        UserDto requestDto = UserDto.builder()
                .name("Анна")
                .email("anna@test.ru")
                .build();

        UserDto resultDto = UserDto.builder()
                .id(1L)
                .name("Анна")
                .email("anna@test.ru")
                .build();

        when(userService.create(any(UserDto.class)))
                .thenReturn(resultDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Анна"))
                .andExpect(jsonPath("$.email").value("anna@test.ru"));
    }

    //Обновление пользователя
    @Test
    void updateShouldReturnUpdatedUser() throws Exception {
        UserDto requestDto = UserDto.builder()
                .name("Новое имя")
                .build();

        UserDto resultDto = UserDto.builder()
                .id(1L)
                .name("Новое имя")
                .email("anna@test.ru")
                .build();

        when(userService.update(eq(1L), any(UserDto.class)))
                .thenReturn(resultDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Новое имя"))
                .andExpect(jsonPath("$.email").value("anna@test.ru"));
    }

    //Получение пользователя по ID
    @Test
    void getByIdShouldReturnUser() throws Exception {
        UserDto resultDto = UserDto.builder()
                .id(1L)
                .name("Анна")
                .email("anna@test.ru")
                .build();

        when(userService.getById(1L))
                .thenReturn(resultDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Анна"))
                .andExpect(jsonPath("$.email").value("anna@test.ru"));
    }

    //Получение всех пользователей
    @Test
    void getAllShouldReturnUsers() throws Exception {
        UserDto firstUser = UserDto.builder()
                .id(1L)
                .name("Анна")
                .email("anna@test.ru")
                .build();

        UserDto secondUser = UserDto.builder()
                .id(2L)
                .name("Иван")
                .email("ivan@test.ru")
                .build();

        when(userService.getAll())
                .thenReturn(List.of(firstUser, secondUser));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    //Удаление пользователя
    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}