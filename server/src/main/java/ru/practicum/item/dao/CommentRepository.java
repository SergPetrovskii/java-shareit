package ru.practicum.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByUserId(Long userId);

    List<Comment> findAllByItemId(Long itemId);
}
