package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemStorage extends JpaRepository<Item, Long> {

    Item save(Item item);

    Optional<Item> findById(Long itemId);

    List<Item> findByOwner_id(Long userId);

    List<Item> findByNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(String name, String description);


    List<Item> findByName(String name);


    void deleteById(Long itemId);
}
