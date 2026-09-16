package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ItemRequestController;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Тесты контроллера запросов вещей
@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    //Создание нового запроса вещи
    @Test
    void createShouldReturnCreatedRequest() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Нужна дрель")
                .build();

        ItemRequestDto resultDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(itemRequestService.create(
                any(Long.class),
                any(ItemRequestDto.class)))
                .thenReturn(resultDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andExpect(jsonPath("$.items").isArray());
    }

    //Получение своих запросов
    @Test
    void getOwnRequestsShouldReturnRequests() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(itemRequestService.getOwnRequests(1L))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"));
    }

    //Получение запросов других пользователей
    @Test
    void getAllRequestsShouldReturnOtherUsersRequests() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(2L)
                .description("Нужен велосипед")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(itemRequestService.getAllRequests(1L))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].description").value("Нужен велосипед"));
    }

    //Получение одного запроса по ID
    @Test
    void getByIdShouldReturnRequest() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(3L)
                .description("Нужен перфоратор")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(itemRequestService.getById(1L, 3L))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/3")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.description").value("Нужен перфоратор"));
    }
}