package com.spring.board.repository;

import com.spring.board.domain.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByBoardIdAndUserId(Long boardId, Long userId);

    List<Report> findByBoardId(Long boardId);

    List<Report> findByUserId(Long userId);

    @Query(value = "select r from Report r join fetch r.board")
    List<Report> findWithBoard(Pageable pageable);

}
