package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

//Тесты преобразования ItemRequestDto в JSON
@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    //Преобразование DTO в JSON
    @Test
    void shouldSerializeItemRequestDto() throws Exception {
        LocalDateTime created =
                LocalDateTime.of(2026, 9, 16, 12, 30, 45);

        ItemRequestItemDto item = ItemRequestItemDto.builder()
                .id(10L)
                .name("Дрель")
                .ownerId(2L)
                .build();

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(created)
                .items(List.of(item))
                .build();

        var result = json.write(requestDto);

        assertThat(result)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(result)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo("Нужна дрель");

        assertThat(result)
                .extractingJsonPathStringValue("$.created")
                .isEqualTo("2026-09-16T12:30:45");

        assertThat(result)
                .extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(10);

        assertThat(result)
                .extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo("Дрель");

        assertThat(result)
                .extractingJsonPathNumberValue("$.items[0].ownerId")
                .isEqualTo(2);
    }

    //Преобразование JSON в DTO
    @Test
    void shouldDeserializeItemRequestDto() throws Exception {
        String content = "{\"id\":1,"
                + "\"description\":\"Нужна дрель\","
                + "\"created\":\"2026-09-16T12:30:45\","
                + "\"items\":["
                + "{\"id\":10,\"name\":\"Дрель\",\"ownerId\":2}"
                + "]}";

        ItemRequestDto result = json.parseObject(content);

        assertEquals(1L, result.getId());
        assertEquals("Нужна дрель", result.getDescription());

        assertEquals(
                LocalDateTime.of(2026, 9, 16, 12, 30, 45),
                result.getCreated()
        );

        assertEquals(1, result.getItems().size());
        assertEquals(10L, result.getItems().get(0).getId());
        assertEquals("Дрель", result.getItems().get(0).getName());
        assertEquals(2L, result.getItems().get(0).getOwnerId());
    }
}