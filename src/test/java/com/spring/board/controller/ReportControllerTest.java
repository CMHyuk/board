package com.spring.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.board.domain.Board;
import com.spring.board.domain.User;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.ReportRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.ReportRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static com.spring.board.Const.LOGIN_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ReportRepository reportRepository;

    @Mock
    private MockHttpSession mockHttpSession;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Board board;

    @BeforeEach
    void set() {
        userRepository.deleteAll();
        boardRepository.deleteAll();
        reportRepository.deleteAll();

        mockHttpSession = new MockHttpSession();

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
    @DisplayName("/board/{boardId}/report 게시글 신고 테스트")
    void reportBoardControllerTest() throws Exception {
        //given
        User user1 = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user1);
        mockHttpSession.setAttribute(LOGIN_USER, user1);

        ReportRequest request = new ReportRequest();
        request.setReportContent("신고");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/report", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(jsonPath("$.nickname").value("닉네임"))
                .andExpect(jsonPath("$.boardId").value(board.getId()))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.reportContent").value("신고"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/report 중복 신고 테스트")
    void duplicateReportTest() throws Exception {
        //given
        User user1 = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user1);
        mockHttpSession.setAttribute(LOGIN_USER, user1);

        ReportRequest request = new ReportRequest();
        request.setReportContent("신고");

        String json = objectMapper.writeValueAsString(request);

        //given
        mockMvc.perform(post("/board/{boardId}/report", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        //when
        mockMvc.perform(post("/board/{boardId}/report", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/report 자신의 게시글 신고 테스트")
    void createReportInvalidRequestTest() throws Exception {
        //given
        mockHttpSession.setAttribute(LOGIN_USER, user);

        ReportRequest request = new ReportRequest();
        request.setReportContent("신고");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/report", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/report 비로그인 회원 신고 테스트")
    void unauthorizedUserReportTest() throws Exception {
        ReportRequest request = new ReportRequest();
        request.setReportContent("신고");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/report", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}