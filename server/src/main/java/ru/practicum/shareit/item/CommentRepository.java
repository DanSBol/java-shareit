package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select co from Comment as co join co.item as i where i.id = ?1 order by co.id")
    List<Comment> getCommentsForItem(long itemId);

    @Query(value = "select co from Comment as co join co.author as com where com.id = ?1 order by co.id")
    List<Comment> getCommentsOfAuthor(long userId);
}