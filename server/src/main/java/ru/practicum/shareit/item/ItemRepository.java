package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

//Репозиторий для работы с вещами
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    //Получение всех вещей владельца
    List<Item> findAllByOwner_Id(Long ownerId);

    //Получение вещей созданных в ответ на запросы
    List<Item> findAllByRequest_IdIn(List<Long> requestIds);

    //Получение вещей созданных в ответ на один запрос
    List<Item> findAllByRequest_Id(Long requestId);

    //Поиск доступных вещей по названию или описанию
    @Query("select i from Item i " +
            "where i.available = true " +
            "and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text);
}