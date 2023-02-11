package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.User;
import com.spring.board.exception.report.DuplicationReportException;
import com.spring.board.exception.report.ReportInvalidRequest;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.ReportRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.report.ReportRequest;
import com.spring.board.response.report.ReportBoardsResponse;
import com.spring.board.response.report.ReportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;

@SpringBootTest
class ReportServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ReportService reportService;

    private User user;
    private Board board;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        boardRepository.deleteAll();
        reportRepository.deleteAll();

        user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user);

        board = Board.builder()
                .title("제목")
                .content("내용")
                .user(user)
                .build();

        boardRepository.save(board);
    }

    @Test
    @Transactional
    @DisplayName("게시판 신고 테스트")
    void reportTest() {
        //given
        User user1 = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user1);

        ReportRequest request = new ReportRequest();
        request.setReportContent("신고");

        //when
        ReportResponse response = reportService.reportBoard(board.getId(), user1.getId(), request);

        //then
        assertEquals(response.getNickname(), user.getNickname());
        assertEquals(response.getBoardId(), board.getId());
        assertEquals(response.getTitle(), board.getTitle());
        assertEquals(response.getContent(), board.getContent());
        assertEquals(response.getReportContent(), request.getReportContent());
    }

    @Test
    @DisplayName("자신이 작성한 게시글 신고 테스트")
    void createReportInvalidRequestTest() {
        //given
        ReportRequest request = new ReportRequest();
        request.setReportContent("신고");

        //expected
        assertThrows(ReportInvalidRequest.class, () -> {
            reportService.reportBoard(board.getId(), user.getId(), request);
        });
    }

    @Test
    @Transactional
    @DisplayName("중복 신고 테스트")
    void duplicateReportTest() {
        //given
        User user1 = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user1);

        ReportRequest request = new ReportRequest();
        request.setReportContent("신고");

        //when
        reportService.reportBoard(board.getId(), user1.getId(), request);

        //expected
        assertThrows(DuplicationReportException.class, () -> {
            reportService.reportBoard(board.getId(), user1.getId(), request);
        });
    }

    @Test
    @Transactional
    @DisplayName("신고 게시글 조회 테스트")
    void getReportBoardsTest() {
        //given
        User user1 = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user1);

        ReportRequest request = new ReportRequest();
        request.setReportContent("신고");

        reportService.reportBoard(board.getId(), user1.getId(), request);

        Pageable pageable = PageRequest.of(0, 10, by(DESC, "id"));

        //when
        List<ReportBoardsResponse> reportBoards = reportService.getReportBoards(pageable);

        //then
        assertEquals(reportBoards.get(0).getBoardId(), board.getId());
        assertEquals(reportBoards.get(0).getTitle(), board.getTitle());
        assertEquals(reportBoards.get(0).getContent(), board.getContent());
        assertEquals(reportBoards.get(0).getReportContent(), request.getReportContent());
    }
}