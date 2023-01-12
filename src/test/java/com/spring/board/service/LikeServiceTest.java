package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.User;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.LikeRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.response.like.LikeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LikeServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    LikeService likeService;

    private User user;
    private Board board;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        boardRepository.deleteAll();
        likeRepository.deleteAll();

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
    }

    @Test
    @DisplayName("좋아요 테스트")
    void likeTest() {
        //when
        LikeResponse response = likeService.like(board.getId(), user.getId());

        //expected
        assertEquals(likeRepository.count(), 1);
        assertEquals(response.getUserNickname(), "닉네임");
        assertEquals(response.getBoardUserNickname(), "닉네임");
        assertEquals(response.getTitle(), "제목");
        assertEquals(response.getContent(), "내용");
    }

    @Test
    @DisplayName("좋아요 취소 테스트")
    void cancelLikeTest() {
        //given
        likeService.like(board.getId(), user.getId());

        //when
        likeService.cancelLike(board.getId(), user.getId());

        //expected
        assertEquals(likeRepository.count(), 0);
    }
}