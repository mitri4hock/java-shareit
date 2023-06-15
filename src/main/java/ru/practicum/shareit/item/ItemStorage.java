package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemStorage extends JpaRepository<Item, Long> {

    Item save(Item item);

    Optional<Item> findById(Long itemId);

    List<Item> findByOwner_idOrderByIdAsc(Long userId);

    @Query("select i from Item i " +
            "where i.available = true " +
            " and (upper(i.name) like upper(?1) or upper(i.description) like upper(?1))")
    List<Item> findByNameOrDescriptionLikeAndAvailableIsTrue(String queryString);

    List<Item> findByName(String name);

    void deleteById(Long itemId);

    long countDistinctByOwner_Id(Long id);


}
