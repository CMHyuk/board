package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.Like;
import com.spring.board.domain.User;
import com.spring.board.exception.board.BoardNotFound;
import com.spring.board.exception.like.LikeNotFound;
import com.spring.board.exception.user.UserNotFound;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.LikeRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.response.like.LikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public LikeResponse like(Long boardId, Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        Optional<Like> findLike = likeRepository.findByBoardIdAndUser(boardId, findUser);

        Like like = Like.builder()
                .board(board)
                .user(findUser)
                .like(findLike)
                .build();

        likeRepository.save(like);

        return LikeResponse.builder()
                .userNickname(findUser.getNickname())
                .boardUserNickname(board.getUser().getNickname())
                .title(board.getTitle())
                .content(board.getContent())
                .build();
    }

    public void cancelLike(Long boardId, Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        Like findLike = likeRepository.findByBoardIdAndUser(boardId, findUser)
                .orElseThrow(LikeNotFound::new);

        likeRepository.delete(findLike);
    }
}
