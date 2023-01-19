package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.Comment;
import com.spring.board.domain.Reply;
import com.spring.board.domain.User;
import com.spring.board.exception.board.BoardNotFound;
import com.spring.board.exception.comment.CommentNotFound;
import com.spring.board.exception.reply.ReplyNotFound;
import com.spring.board.exception.user.UserNotFound;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.CommentRepository;
import com.spring.board.repository.ReplyRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.reply.EditReplyRequest;
import com.spring.board.request.reply.WriteReplyRequest;
import com.spring.board.response.reply.EditReplyResponse;
import com.spring.board.response.reply.WriteReplyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    public WriteReplyResponse writeReply(User user, Long boardId, Long commentId, WriteReplyRequest request) {
        User findUser = userRepository.findById(user.getId())
                .orElseThrow(UserNotFound::new);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFound::new);

        Reply reply = Reply.builder()
                .reply(request.getReply())
                .user(findUser)
                .board(board)
                .comment(comment)
                .build();

        Reply savedReply = replyRepository.save(reply);

        return new WriteReplyResponse(findUser.getNickname(), savedReply.getReply());
    }

    public EditReplyResponse editReply(User user, Long boardId, Long commentId,
                                      Long replyId, EditReplyRequest request) {
        Reply reply = validateAndGetReply(user, boardId, commentId, replyId);
        reply.updateReply(request.getReply());
        return new EditReplyResponse(user.getNickname(), reply.getReply());
    }

    public void deleteReply(User user, Long boardId, Long commentId, Long replyId) {
        Reply reply = validateAndGetReply(user, boardId, commentId, replyId);
        replyRepository.delete(reply);
    }

    private Reply validateAndGetReply(User user, Long boardId, Long commentId, Long replyId) {
        userRepository.findById(user.getId())
                .orElseThrow(UserNotFound::new);

        boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        commentRepository.findById(commentId)
                .orElseThrow(CommentNotFound::new);

        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(ReplyNotFound::new);
        return reply;
    }
}
