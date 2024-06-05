package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select it from Item as it join it.owner as u where u.id = ?1 order by it.id")
    List<Item> getItemsByOwner(long userId);

    List<Item> findByAvailableAndDescriptionContainingIgnoreCaseOrderById(Boolean available, String text);
}