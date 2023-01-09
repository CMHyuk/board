package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.Comment;
import com.spring.board.domain.User;
import com.spring.board.exception.BoardNotFound;
import com.spring.board.exception.CommentNotFound;
import com.spring.board.exception.InvalidRequest;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.CommentRepository;
import com.spring.board.request.comment.EditCommentRequest;
import com.spring.board.request.comment.WriteCommentRequest;
import com.spring.board.response.comment.EditCommentResponse;
import com.spring.board.response.comment.SaveCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public SaveCommentResponse writeComment(Long boardId, WriteCommentRequest request, User user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        Comment comment = Comment.builder()
                .comment(request.getComment())
                .board(board)
                .user(user)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return new SaveCommentResponse(savedComment.getComment(), savedComment.getUser().getNickname());
    }

    public EditCommentResponse editComment(Long boardId, Long commentId, User user, EditCommentRequest request) {
        Comment comment = validateAndGetComment(boardId, commentId, user);
        comment.changeComment(request.getComment());
        return new EditCommentResponse(comment.getComment());
    }

    public void deleteComment(Long boardId, Long commentId, User user) {
        Comment comment = validateAndGetComment(boardId, commentId, user);
        commentRepository.delete(comment);
    }

    private Comment validateAndGetComment(Long boardId, Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFound::new);

        boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        Long commentUserId = comment.getUser().getId();
        Long userId = user.getId();

        if (commentUserId != userId) {
            throw new InvalidRequest();
        }

        return comment;
    }
}