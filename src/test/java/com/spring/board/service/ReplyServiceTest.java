package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.Comment;
import com.spring.board.domain.Reply;
import com.spring.board.domain.User;
import com.spring.board.exception.BoardNotFound;
import com.spring.board.exception.CommentNotFound;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.CommentRepository;
import com.spring.board.repository.ReplyRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.reply.EditReplyRequest;
import com.spring.board.request.reply.WriteReplyRequest;
import com.spring.board.response.reply.EditReplyResponse;
import com.spring.board.response.reply.WriteReplyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ReplyServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ReplyRepository replyRepository;

    @Autowired
    ReplyService replyService;

    private User user;
    private Board board;
    private Comment comment;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        boardRepository.deleteAll();
        commentRepository.deleteAll();
        replyRepository.deleteAll();

        user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("패스워드")
                .build();

        userRepository.save(user);

        board = Board.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .build();

        boardRepository.save(board);

        comment = Comment.builder()
                .user(user)
                .board(board)
                .comment("댓글")
                .build();

        commentRepository.save(comment);
    }

    @Test
    @DisplayName("대댓글 작성 테스트")
    void writeReplyTest() {
        //given
        WriteReplyRequest request = new WriteReplyRequest();
        request.setReply("대댓글");

        //when
        WriteReplyResponse replyResponse = replyService.writeReply(user, board.getId(), comment.getId(), request);

        //then
        assertEquals(replyResponse.getNickname(), "닉네임");
        assertEquals(replyResponse.getReply(), "대댓글");
    }

    @Test
    @DisplayName("대댓글 작성 실패 테스트")
    void writeReplyFailTest() {
        //given
        WriteReplyRequest request = new WriteReplyRequest();
        request.setReply("대댓글");

        //when
        assertThrows(BoardNotFound.class, () -> {
            replyService.writeReply(user, 100L, comment.getId(), request);
        });

        assertThrows(CommentNotFound.class, () -> {
            replyService.writeReply(user, board.getId(), 100L, request);
        });
    }

    @Test
    @DisplayName("대댓글 수정 테스트")
    void editReplyTest() {
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

        //when
        EditReplyResponse replyResponse =
                replyService.editReply(user, board.getId(), comment.getId(), reply.getId(), request);

        //then
        assertEquals(replyResponse.getNickname(), "닉네임");
        assertEquals(replyResponse.getReply(), "대댓글수정");
    }

    @Test
    @DisplayName("대댓글 수정 실패 테스트")
    void editReplyFailTest() {
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

        //expected
        assertThrows(BoardNotFound.class, () -> {
            replyService.editReply(user, 100L, comment.getId(), reply.getId(), request);
        });

        assertThrows(CommentNotFound.class, () -> {
            replyService.editReply(user, board.getId(), 100L, reply.getId(), request);
        });
    }

    @Test
    @DisplayName("대댓글 삭제 테스트")
    void deleteReplyTest() {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("대댓글")
                .build();

        replyRepository.save(reply);

        //when
        replyService.deleteReply(user, board.getId(), comment.getId(), reply.getId());

        //then
        assertEquals(replyRepository.count(), 0);
    }

    @Test
    @DisplayName("대댓글 삭제 실패 테스트")
    void deleteReplyFailTest() {
        //given
        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .comment(comment)
                .reply("대댓글")
                .build();

        replyRepository.save(reply);

        //expected
        assertThrows(BoardNotFound.class, () -> {
            replyService.deleteReply(user, 100L, comment.getId(), reply.getId());
        });

        assertThrows(CommentNotFound.class, () -> {
            replyService.deleteReply(user, board.getId(), 100L, reply.getId());
        });
    }
}