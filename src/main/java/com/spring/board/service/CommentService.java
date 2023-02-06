package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.Comment;
import com.spring.board.domain.Reply;
import com.spring.board.domain.User;
import com.spring.board.exception.board.BoardNotFound;
import com.spring.board.exception.comment.CommentNotFound;
import com.spring.board.exception.InvalidRequest;
import com.spring.board.exception.user.UserNotFound;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.CommentRepository;
import com.spring.board.repository.ReplyRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.comment.EditCommentRequest;
import com.spring.board.request.comment.WriteCommentRequest;
import com.spring.board.response.comment.EditCommentResponse;
import com.spring.board.response.comment.SaveCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;

    public SaveCommentResponse writeComment(Long boardId, WriteCommentRequest request, User user) {
        User findUser = userRepository.findById(user.getId())
                .orElseThrow(UserNotFound::new);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        Comment comment = Comment.builder()
                .comment(request.getComment())
                .board(board)
                .user(findUser)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return SaveCommentResponse.builder()
                .comment(savedComment.getComment())
                .nickname(savedComment.getUser().getNickname())
                .build();
    }

    public EditCommentResponse editComment(Long boardId, Long commentId, User user, EditCommentRequest request) {
        Comment comment = validateAndGetComment(boardId, commentId, user);
        comment.changeComment(request.getComment());
        return new EditCommentResponse(comment.getComment());
    }

    public void deleteComment(Long boardId, Long commentId, User user) {
        Comment comment = commentRepository.findCommentWithUserAndBoard(commentId)
                .orElseThrow(CommentNotFound::new);

        List<Reply> replies = replyRepository.findByCommentId(commentId);

        if (!comment.getBoard().getId().equals(boardId)) {
            throw new BoardNotFound();
        }

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new InvalidRequest();
        }

        replyRepository.deleteAllInBatch(replies);
        commentRepository.delete(comment);
    }

    private Comment validateAndGetComment(Long boardId, Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFound::new);

        boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        Long commentUserId = comment.getUser().getId();
        Long userId = user.getId();

        if (!commentUserId.equals(userId)) {
            throw new InvalidRequest();
        }

        return comment;
    }
}
