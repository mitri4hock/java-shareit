package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Repository
public interface CommentStorage extends JpaRepository<Comment, Long> {

    Comment save(Comment comment);

    List<Comment> findByItem_Id(Long id);

}
