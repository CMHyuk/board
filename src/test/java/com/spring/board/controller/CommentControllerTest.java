package com.spring.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.board.domain.Board;
import com.spring.board.domain.Comment;
import com.spring.board.domain.User;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.CommentRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.comment.EditCommentRequest;
import com.spring.board.request.comment.WriteCommentRequest;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
class CommentControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    MockHttpSession mockHttpSession;

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
        commentRepository.deleteAll();
        mockHttpSession = new MockHttpSession();

        user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        board = Board.builder()
                .title("제목")
                .content("내용")
                .user(user)
                .build();
    }

    @Test
    @DisplayName("/board/{boardId}/comment 댓글 작성 테스트")
    void writeCommentControllerTest() throws Exception {
        //given
        userRepository.save(user);
        boardRepository.save(board);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        WriteCommentRequest request = new WriteCommentRequest();
        request.setComment("댓글");

        String json = objectMapper.writeValueAsString(request);

        //excepted
        mockMvc.perform(post("/board/{boardId}/comment", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(jsonPath("$.comment").value("댓글"))
                .andExpect(jsonPath("$.nickname").value("닉네임"))
                .andExpect(status().isOk())
                .andDo(document("comment-write",
                        requestFields(
                                fieldWithPath("comment").description("댓글")
                        )));
    }

    @Test
    @DisplayName("/board/{boardId}/comment 미인증 사용자 테스트")
    void unauthorizedUserWriteCommentTest() throws Exception {
        //given
        userRepository.save(user);
        boardRepository.save(board);

        WriteCommentRequest request = new WriteCommentRequest();
        request.setComment("댓글");

        String json = objectMapper.writeValueAsString(request);

        //excepted
        mockMvc.perform(post("/board/{boardId}/comment", board.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andDo(document("comment-unauthorized"));
    }

    @Test
    @DisplayName("/board/{boardId}/comment 존재하지 않는 글에 댓글 작성")
    void NonExistBoardWriteCommentTest() throws Exception {
        //given
        userRepository.save(user);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        WriteCommentRequest request = new WriteCommentRequest();
        request.setComment("댓글");

        String json = objectMapper.writeValueAsString(request);

        //excepted
        mockMvc.perform(post("/board/{boardId}/comment", 0L)
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(document("comment-boardNotFound"));
    }

    @Test
    @DisplayName("/board/{boardId}/editComment/{commentId} 댓글 수정 테스트")
    void editCommentControllerTest() throws Exception {
        //given
        userRepository.save(user);
        boardRepository.save(board);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        Comment comment = Comment.builder()
                .comment("댓글")
                .user(user)
                .board(board)
                .build();

        commentRepository.save(comment);

        EditCommentRequest request = new EditCommentRequest();
        request.setComment("댓글수정");

        String json = objectMapper.writeValueAsString(request);

        //excepted
        mockMvc.perform(patch("/board/{boardId}/editComment/{commentId}", board.getId(), comment.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(jsonPath("$.comment").value("댓글수정"))
                .andExpect(status().isOk())
                .andDo(document("comment-edit",
                        requestFields(
                                fieldWithPath("comment").description("댓글 수정")
                        )));
    }

    @Test
    @DisplayName("/board/{boardId}/editComment/{commentId} 미인증 사용자 댓글 수정")
    void unauthorizedUserEditCommentTest() throws Exception {
        //given
        userRepository.save(user);
        boardRepository.save(board);

        Comment comment = Comment.builder()
                .comment("댓글")
                .user(user)
                .board(board)
                .build();

        commentRepository.save(comment);

        EditCommentRequest request = new EditCommentRequest();
        request.setComment("댓글");

        String json = objectMapper.writeValueAsString(request);

        //excepted
        mockMvc.perform(patch("/board/{boardId}/editComment/{commentId}", board.getId(), comment.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/editComment/{commentId} 존재하지 않는 글의 댓글 수정")
    void NonExistBoardEditCommentTest() throws Exception {
        //given
        userRepository.save(user);
        boardRepository.save(board);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        Comment comment = Comment.builder()
                .comment("댓글")
                .user(user)
                .board(board)
                .build();

        commentRepository.save(comment);

        EditCommentRequest request = new EditCommentRequest();
        request.setComment("댓글수정");

        String json = objectMapper.writeValueAsString(request);

        //excepted
        mockMvc.perform(patch("/board/{boardId}/editComment/{commentId}", 100L, comment.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/editComment/{commentId} 댓글 수정 미입력 검증")
    void validateCommentTest() throws Exception {
        //given
        userRepository.save(user);
        boardRepository.save(board);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        Comment comment = Comment.builder()
                .comment("댓글")
                .user(user)
                .board(board)
                .build();

        commentRepository.save(comment);

        EditCommentRequest request = new EditCommentRequest();
        request.setComment("");

        String json = objectMapper.writeValueAsString(request);

        //excepted
        mockMvc.perform(patch("/board/{boardId}/editComment/{commentId}", board.getId(), comment.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(document("comment-edit-validation"));
    }

    @Test
    @DisplayName("/board/{boardId}/editComment/{commentId} 존재하지 않는 댓글 수정")
    void EditNonExistCommentTest() throws Exception {
        //given
        userRepository.save(user);
        boardRepository.save(board);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        Comment comment = Comment.builder()
                .comment("댓글")
                .user(user)
                .board(board)
                .build();

        commentRepository.save(comment);

        EditCommentRequest request = new EditCommentRequest();
        request.setComment("댓글수정");

        String json = objectMapper.writeValueAsString(request);

        //excepted
        mockMvc.perform(patch("/board/{boardId}/editComment/{commentId}", board.getId(), 0L)
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(document("comment-notFound",
                        requestFields(
                                fieldWithPath("comment").description("댓글 수정")
                        )));
    }

    @Test
    @DisplayName("/board/{boardId}/deleteComment/{commentId} 댓글 삭제 테스트")
    void deleteCommentControllerTest() throws Exception {
        //given
        userRepository.save(user);
        boardRepository.save(board);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        Comment comment = Comment.builder()
                .comment("댓글")
                .user(user)
                .board(board)
                .build();

        commentRepository.save(comment);

        //when
        mockMvc.perform(delete("/board/{boardId}/deleteComment/{commentId}", board.getId(), comment.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andDo(document("comment-delete"));

        //then
        assertEquals(commentRepository.count(), 0);
    }

    @Test
    @DisplayName("/board/{boardId}/deleteComment/{commentId} 미인증 사용자 댓글 삭제 테스트")
    void unauthorizedUserDeleteCommentTest() throws Exception {
        //given
        userRepository.save(user);
        boardRepository.save(board);

        Comment comment = Comment.builder()
                .comment("댓글")
                .user(user)
                .board(board)
                .build();

        commentRepository.save(comment);

        //when
        mockMvc.perform(delete("/board/{boardId}/deleteComment/{commentId}", board.getId(), comment.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/deleteComment/{commentId} 존재하지 않는 글의 댓글 삭제 테스트")
    void deleteCommentInNonExistBoard() throws Exception {
        //given
        userRepository.save(user);
        boardRepository.save(board);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        Comment comment = Comment.builder()
                .comment("댓글")
                .user(user)
                .board(board)
                .build();

        commentRepository.save(comment);

        //when
        mockMvc.perform(delete("/board/{boardId}/deleteComment/{commentId}", 100L, comment.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/deleteComment/{commentId} 존재하지 않는 댓글 삭제 테스트")
    void deleteNonExistComment() throws Exception {
        //given
        userRepository.save(user);
        boardRepository.save(board);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        Comment comment = Comment.builder()
                .comment("댓글")
                .user(user)
                .board(board)
                .build();

        commentRepository.save(comment);

        //when
        mockMvc.perform(delete("/board/{boardId}/deleteComment/{commentId}", board.getId(), 100L)
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}