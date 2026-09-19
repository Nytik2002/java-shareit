package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Тесты контроллера вещей
@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    //Создание вещи
    @Test
    void createShouldCallServiceAndReturnCreated() throws Exception {
        when(itemService.create(eq(1L), any(ItemDto.class)))
                .thenReturn(null);

        String json = "{\"name\":\"Дрель\","
                + "\"description\":\"Обычная дрель\","
                + "\"available\":true}";

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        ArgumentCaptor<ItemDto> captor =
                ArgumentCaptor.forClass(ItemDto.class);

        verify(itemService).create(eq(1L), captor.capture());

        assertEquals("Дрель", captor.getValue().getName());
        assertEquals(
                "Обычная дрель",
                captor.getValue().getDescription()
        );
    }

    //Обновление вещи
    @Test
    void updateShouldCallService() throws Exception {
        when(itemService.update(
                eq(1L),
                eq(10L),
                any(ItemDto.class)))
                .thenReturn(null);

        String json = "{\"name\":\"Новая дрель\"}";

        mockMvc.perform(patch("/items/10")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        ArgumentCaptor<ItemDto> captor =
                ArgumentCaptor.forClass(ItemDto.class);

        verify(itemService).update(
                eq(1L),
                eq(10L),
                captor.capture()
        );

        assertEquals(
                "Новая дрель",
                captor.getValue().getName()
        );
    }

    //Получение вещи по ID
    @Test
    void getByIdShouldCallService() throws Exception {
        when(itemService.getById(1L, 10L))
                .thenReturn(null);

        mockMvc.perform(get("/items/10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemService).getById(1L, 10L);
    }

    //Получение всех вещей владельца
    @Test
    void getAllByOwnerShouldReturnItems() throws Exception {
        when(itemService.getAllByOwner(1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(itemService).getAllByOwner(1L);
    }

    //Поиск вещей
    @Test
    void searchShouldReturnItems() throws Exception {
        when(itemService.search("дрель"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(itemService).search("дрель");
    }

    //Добавление комментария
    @Test
    void addCommentShouldCallService() throws Exception {
        when(itemService.addComment(
                eq(1L),
                eq(10L),
                any(CommentDto.class)))
                .thenReturn(null);

        String json = "{\"text\":\"Отличная вещь\"}";

        mockMvc.perform(post("/items/10/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        ArgumentCaptor<CommentDto> captor =
                ArgumentCaptor.forClass(CommentDto.class);

        verify(itemService).addComment(
                eq(1L),
                eq(10L),
                captor.capture()
        );

        assertEquals(
                "Отличная вещь",
                captor.getValue().getText()
        );
    }
}