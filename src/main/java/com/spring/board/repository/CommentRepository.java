package com.spring.board.repository;

import com.spring.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select c from Comment c join fetch c.user join fetch c.board where c.id =:id")
    Optional<Comment> findCommentWithUserAndBoard(@Param("id") Long id);

    List<Comment> findByBoardId(Long id);
}
