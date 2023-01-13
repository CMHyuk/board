package com.spring.board.repository;

import com.spring.board.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByBoardIdAndUserId(Long boardId, Long userId);

}
