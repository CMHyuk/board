package com.spring.board.repository;

import com.spring.board.domain.Board;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query(value = "select b from Board b join fetch b.user where b.title like %:title%")
    List<Board> findByTitleContaining(@Param("title") String title, Pageable pageable);

    @Query(value = "select b from Board b join fetch b.user")
    List<Board> findBoardWithUser(Pageable pageable);

    @Query(value = "select b from Board b join fetch b.user left join fetch b.comments where b.id =:id")
    Optional<Board> findWithAll(@Param("id") Long id);

}
