package com.spring.board.repository;

import com.spring.board.domain.Like;
import com.spring.board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByBoardIdAndUser(Long boardId, User user);

    List<Like> findByUserId(Long userId);
}
