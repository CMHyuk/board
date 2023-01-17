package com.spring.board.repository;

import com.spring.board.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByBoardIdAndUserId(Long boardId, Long userId);

    List<Like> findByUserId(Long userId);
}
