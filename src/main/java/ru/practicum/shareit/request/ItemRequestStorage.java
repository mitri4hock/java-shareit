package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {

    ItemRequest save(ItemRequest itemRequest);

    List<ItemRequest> findByRequestor_IdOrderByCreatedDesc(Long id);

    List<ItemRequest> findAllByOrderByCreatedDesc();

    @Override
    Optional<ItemRequest> findById(Long requestId);

    @Query("select i from ItemRequest i where i.requestor.id != ?1")
    List<ItemRequest> findByRequestor_Id(Long id, Pageable pageable);




}
