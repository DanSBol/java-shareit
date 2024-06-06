package ru.practicum.shareit.item;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select it from Item as it join it.owner as u where u.id = ?1 order by it.id")
    Page<Item> getItemsByOwner(long userId, Pageable pageable);

    Page<Item> findByAvailableAndDescriptionContainingIgnoreCaseOrderById(Boolean available, String text,
                                                                          Pageable pageable);

    List<Item> findByRequestId(Long requestId);
}