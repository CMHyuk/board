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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static com.spring.board.Const.LOGIN_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
class ReplyControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ReplyRepository replyRepository;

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    MockHttpSession mockHttpSession;

    private User user;
    private Board board;
    private Comment comment;

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
        replyRepository.deleteAll();

        mockHttpSession = new MockHttpSession();

        user = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        board = Board.builder()
                .title("??????")
                .content("??????")
                .user(user)
                .build();

        comment = Comment.builder()
                .comment("??????")
                .user(user)
                .board(board)
                .build();

        userRepository.save(user);
        boardRepository.save(board);
        commentRepository.save(comment);
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/reply ????????? ?????? ?????????")
    void writeReplyTest() throws Exception {
        //given
        WriteReplyRequest request = new WriteReplyRequest();
        request.setReply("?????????");

        mockHttpSession.setAttribute(LOGIN_USER, user);
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/{commentId}/reply", board.getId(), comment.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(jsonPath("$.nickname").value("?????????"))
                .andExpect(jsonPath("$.reply").value("?????????"))
                .andExpect(status().isOk())
                .andDo(document("reply-write",
                        requestFields(
                                fieldWithPath("reply").description("?????????")
                        )));
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/reply ????????? ?????? ?????? ?????????")
    void writeReplyFailTest() throws Exception {
        //given
        WriteReplyRequest request = new WriteReplyRequest();
        request.setReply("?????????");

        mockHttpSession.setAttribute(LOGIN_USER, user);
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/{commentId}/reply", 100L, comment.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(document("reply-notFound"));
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/reply ????????? ????????? ????????? ?????? ?????????")
    void unauthorizedUserWriteReplyTest() throws Exception {
        //given
        WriteReplyRequest request = new WriteReplyRequest();
        request.setReply("?????????");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/{boardId}/{commentId}/reply", board.getId(), comment.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andDo(document("reply-unauthorized"));
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/editReply/{replyId} ????????? ?????? ?????????")
    void editReplyTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("?????????")
                .build();

        replyRepository.save(reply);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        EditReplyRequest request = new EditReplyRequest();
        request.setReply("???????????????");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/board/{boardId}/{commentId}/editReply/{replyId}",
                        board.getId(), comment.getId(), reply.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(jsonPath("$.nickname").value("?????????"))
                .andExpect(jsonPath("$.reply").value("???????????????"))
                .andExpect(status().isOk())
                .andDo(document("reply-edit",
                        requestFields(
                                fieldWithPath("reply").description("????????? ??????")
                        )));
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/editReply/{replyId} ????????? ?????? ?????? ?????????")
    void validateEditReplyTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("?????????")
                .build();

        replyRepository.save(reply);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        EditReplyRequest request = new EditReplyRequest();
        request.setReply("");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/board/{boardId}/{commentId}/editReply/{replyId}",
                        board.getId(), comment.getId(), reply.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(document("reply-edit-validation"));
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/editReply/{replyId} ????????? ?????? ?????? ?????????")
    void editReplyFailTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("?????????")
                .build();

        replyRepository.save(reply);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        EditReplyRequest request = new EditReplyRequest();
        request.setReply("???????????????");

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
    @DisplayName("/board/{boardId}/{commentId}/editReply/{replyId} ????????? ????????? ????????? ?????? ?????????")
    void unauthorizedUserEditReplyTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("?????????")
                .build();

        replyRepository.save(reply);

        EditReplyRequest request = new EditReplyRequest();
        request.setReply("???????????????");

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
    @DisplayName("/board/{boardId}/{commentId}/deleteReply/{replyId} ????????? ?????? ?????????")
    void deleteReplyTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("?????????")
                .build();

        replyRepository.save(reply);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        //expected
        mockMvc.perform(delete("/board/{boardId}/{commentId}/deleteReply/{replyId}",
                        board.getId(), comment.getId(), reply.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andDo(document("reply-delete"));
    }

    @Test
    @DisplayName("/board/{boardId}/{commentId}/deleteReply/{replyId} ????????? ?????? ?????? ?????????")
    void deleteReplyFailTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("?????????")
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
    @DisplayName("/board/{boardId}/{commentId}/deleteReply/{replyId} ????????? ????????? ????????? ?????? ?????????")
    void unauthorizedUserDeleteReplyTest() throws Exception {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("?????????")
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