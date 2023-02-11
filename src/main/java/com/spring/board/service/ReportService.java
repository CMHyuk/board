package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.Report;
import com.spring.board.domain.User;
import com.spring.board.exception.board.BoardNotFound;
import com.spring.board.exception.user.UserNotFound;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.ReportRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.report.ReportRequest;
import com.spring.board.response.report.ReportBoardsResponse;
import com.spring.board.response.report.ReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ReportRepository reportRepository;

    public ReportResponse reportBoard(Long boardId, Long userId, ReportRequest request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        Optional<Report> findReport = reportRepository.findByBoardIdAndUserId(boardId, userId);

        Report report = Report.builder()
                .reportContent(request.getReportContent())
                .user(user)
                .board(board)
                .report(findReport)
                .build();

        reportRepository.save(report);

        return ReportResponse.builder()
                .nickname(user.getNickname())
                .boardId(boardId)
                .title(board.getTitle())
                .content(board.getContent())
                .reportContent(report.getReportContent())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReportBoardsResponse> getReportBoards(Pageable pageable) {
        List<Report> reports = reportRepository.findWithBoard(pageable);
        return reports.stream()
                .map(r -> ReportBoardsResponse.builder()
                        .reportId(r.getId())
                        .reporter(r.getUser().getNickname())
                        .reportContent(r.getReportContent())
                        .boardId(r.getBoard().getId())
                        .title(r.getBoard().getTitle())
                        .content(r.getBoard().getContent())
                        .build())
                .collect(Collectors.toList());
    }
}
