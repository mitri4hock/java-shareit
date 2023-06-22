package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {

    ItemRequest save(ItemRequest itemRequest);

    List<ItemRequest> findByRequestor_IdOrderByCreatedDesc(Long id);

}
