package com.spring.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.board.domain.Board;
import com.spring.board.domain.User;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.ReportRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.report.ReportRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static com.spring.board.Const.LOGIN_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
class ReportControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ReportRepository reportRepository;

    @Mock
    private MockHttpSession mockHttpSession;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Board board;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentation) {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        userRepository.deleteAll();
        boardRepository.deleteAll();
        reportRepository.deleteAll();

        mockHttpSession = new MockHttpSession();

        user = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        userRepository.save(user);

        board = Board.builder()
                .title("??????")
                .content("??????")
                .user(user)
                .build();

        boardRepository.save(board);
    }

    @Test
    @DisplayName("/board/{boardId}/report ????????? ?????? ?????????")
    void reportBoardControllerTest() throws Exception {
        //given
        User user1 = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        userRepository.save(user1);
        mockHttpSession.setAttribute(LOGIN_USER, user1);

        ReportRequest request = new ReportRequest();
        request.setReportContent("??????");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/report", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(jsonPath("$.nickname").value("?????????"))
                .andExpect(jsonPath("$.boardId").value(board.getId()))
                .andExpect(jsonPath("$.title").value("??????"))
                .andExpect(jsonPath("$.content").value("??????"))
                .andExpect(jsonPath("$.reportContent").value("??????"))
                .andExpect(status().isOk())
                .andDo(document("board-report",
                        requestFields(
                                fieldWithPath("reportContent").description("??????")
                        )));
    }

    @Test
    @DisplayName("/board/{boardId}/report ?????? ?????? ?????????")
    void validateReportTest() throws Exception {
        //given
        User user1 = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        userRepository.save(user1);
        mockHttpSession.setAttribute(LOGIN_USER, user1);

        ReportRequest request = new ReportRequest();
        request.setReportContent("");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/report", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(document("board-report-validation"));
    }

    @Test
    @DisplayName("/board/{boardId}/report ?????? ?????? ?????????")
    void duplicateReportTest() throws Exception {
        //given
        User user1 = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        userRepository.save(user1);
        mockHttpSession.setAttribute(LOGIN_USER, user1);

        ReportRequest request = new ReportRequest();
        request.setReportContent("??????");

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
                .andDo(document("board-reportDuplication",
                        requestFields(
                                fieldWithPath("reportContent").description("??????")
                        )));
    }

    @Test
    @DisplayName("/board/{boardId}/report ????????? ????????? ?????? ?????????")
    void createReportInvalidRequestTest() throws Exception {
        //given
        mockHttpSession.setAttribute(LOGIN_USER, user);

        ReportRequest request = new ReportRequest();
        request.setReportContent("??????");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/report", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(document("board-badRequest",
                        requestFields(
                                fieldWithPath("reportContent").description("??????")
                        )));
    }

    @Test
    @DisplayName("/board/{boardId}/report ???????????? ?????? ?????? ?????????")
    void unauthorizedUserReportTest() throws Exception {
        ReportRequest request = new ReportRequest();
        request.setReportContent("??????");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/report", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andDo(document("board-unauthorized",
                        requestFields(
                                fieldWithPath("reportContent").description("??????")
                        )));
    }
}