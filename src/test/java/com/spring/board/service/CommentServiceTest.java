package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.Comment;
import com.spring.board.domain.User;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.CommentRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.comment.EditCommentRequest;
import com.spring.board.request.comment.WriteCommentRequest;
import com.spring.board.response.comment.EditCommentResponse;
import com.spring.board.response.comment.SaveCommentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    private User user;
    private Board board;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        boardRepository.deleteAll();
        commentRepository.deleteAll();

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
    @DisplayName("댓글 작성 테스트")
    void writeCommentTest() {
        //given
        User savedUser = userRepository.save(user);
        Board savedBoard = boardRepository.save(board);

        WriteCommentRequest request = new WriteCommentRequest();
        request.setComment("댓글");

        //when
        SaveCommentResponse response =
                commentService.writeComment(savedBoard.getId(), request, savedUser);

        //then
        assertEquals(response.getComment(), "댓글");
        assertEquals(response.getNickname(), "닉네임");
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void editCommentTest() {
        //given
        User savedUser = userRepository.save(user);
        Board savedBoard = boardRepository.save(board);

        Comment comment = Comment.builder()
                .comment("댓글")
                .user(savedUser)
                .board(savedBoard)
                .build();

        commentRepository.save(comment);

        EditCommentRequest request = new EditCommentRequest();
        request.setComment("댓글수정");

        //when
        EditCommentResponse response =
                commentService.editComment(savedBoard.getId(), comment.getId(), savedUser, request);

        //then
        assertEquals(response.getComment(), "댓글수정");
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteCommentTest() {
        //given
        User savedUser = userRepository.save(user);
        Board savedBoard = boardRepository.save(board);

        Comment comment = Comment.builder()
                .comment("댓글")
                .user(savedUser)
                .board(savedBoard)
                .build();

        commentRepository.save(comment);

        //when
        commentService.deleteComment(savedBoard.getId(), comment.getId(), savedUser);

        //then
        assertEquals(commentRepository.count(), 0);
    }
}