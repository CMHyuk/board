package com.spring.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.board.domain.Board;
import com.spring.board.domain.Comment;
import com.spring.board.domain.Reply;
import com.spring.board.domain.User;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.CommentRepository;
import com.spring.board.repository.ReplyRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.reply.EditReplyRequest;
import com.spring.board.request.reply.WriteReplyRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.spring.board.Const.LOGIN_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ReplyControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ReplyRepository replyRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    MockHttpSession mockHttpSession;

    private User user;
    private Board board;
    private Comment comment;

    @BeforeEach
    void set() {
        userRepository.deleteAll();
        boardRepository.deleteAll();
        commentRepository.deleteAll();
        replyRepository.deleteAll();

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

        comment = Comment.builder()
                .comment("댓글")
                .user(user)
                .board(board)
                .build();

        userRepository.save(user);
        boardRepository.save(board);
        commentRepository.save(comment);
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/reply 대댓글 작성 테스트")
    void writeReplyTest() throws Exception {
        //given
        WriteReplyRequest request = new WriteReplyRequest();
        request.setReply("대댓글");

        mockHttpSession.setAttribute(LOGIN_USER, user);
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/{commentId}/reply", board.getId(), comment.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(jsonPath("$.nickname").value("닉네임"))
                .andExpect(jsonPath("$.reply").value("대댓글"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/reply 대댓글 작성 실패 테스트")
    void writeReplyFailTest() throws Exception {
        //given
        WriteReplyRequest request = new WriteReplyRequest();
        request.setReply("대댓글");

        mockHttpSession.setAttribute(LOGIN_USER, user);
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/{commentId}/reply", 100L, comment.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/reply 미인증 사용자 대댓글 작성 테스트")
    void unauthorizedUserWriteReplyTest() throws Exception {
        //given
        WriteReplyRequest request = new WriteReplyRequest();
        request.setReply("대댓글");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/{commentId}/reply", board.getId(), comment.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/editReply/{replyId} 대댓글 수정 테스트")
    void editReplyTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("대댓글")
                .build();

        replyRepository.save(reply);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        EditReplyRequest request = new EditReplyRequest();
        request.setReply("대댓글수정");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/board/{boardId}/{commentId}/editReply/{replyId}",
                        board.getId(), comment.getId(), reply.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(jsonPath("$.nickname").value("닉네임"))
                .andExpect(jsonPath("$.reply").value("대댓글수정"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/editReply/{replyId} 대댓글 수정 실패 테스트")
    void editReplyFailTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("대댓글")
                .build();

        replyRepository.save(reply);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        EditReplyRequest request = new EditReplyRequest();
        request.setReply("대댓글수정");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/board/{boardId}/{commentId}/editReply/{replyId}",
                        100L, comment.getId(), reply.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/editReply/{replyId} 미인증 사용자 대댓글 수정 테스트")
    void unauthorizedUserEditReplyTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("대댓글")
                .build();

        replyRepository.save(reply);

        EditReplyRequest request = new EditReplyRequest();
        request.setReply("대댓글수정");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/board/{boardId}/{commentId}/editReply/{replyId}",
                        board.getId(), comment.getId(), reply.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/deleteReply/{replyId} 대댓글 삭제 테스트")
    void deleteReplyTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("대댓글")
                .build();

        replyRepository.save(reply);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        //expected
        mockMvc.perform(delete("/board/{boardId}/{commentId}/deleteReply/{replyId}",
                        board.getId(), comment.getId(), reply.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/deleteReply/{replyId} 대댓글 삭제 실패 테스트")
    void deleteReplyFailTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("대댓글")
                .build();

        replyRepository.save(reply);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        //expected
        mockMvc.perform(delete("/board/{boardId}/{commentId}/deleteReply/{replyId}",
                        100L, comment.getId(), reply.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/deleteReply/{replyId} 미인증 사용자 대댓글 삭제 테스트")
    void unauthorizedUserDeleteReplyTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("대댓글")
                .build();

        replyRepository.save(reply);

        //expected
        mockMvc.perform(delete("/board/{boardId}/{commentId}/deleteReply/{replyId}",
                        board.getId(), comment.getId(), reply.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}