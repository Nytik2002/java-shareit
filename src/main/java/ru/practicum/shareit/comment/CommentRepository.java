package ru.practicum.shareit.comment;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

//Репозиторий для работы с комментариями
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    //Получение комментариев одной вещи
    List<Comment> findAllByItem_IdOrderByCreatedAsc(Long itemId);

    //Получение комментариев для списка вещей
    List<Comment> findByItemIn(List<Item> items, Sort sort);
}