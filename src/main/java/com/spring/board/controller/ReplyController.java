package com.spring.board.controller;

import com.spring.board.domain.User;
import com.spring.board.request.reply.EditReplyRequest;
import com.spring.board.request.reply.WriteReplyRequest;
import com.spring.board.response.reply.EditReplyResponse;
import com.spring.board.response.reply.WriteReplyResponse;
import com.spring.board.service.ReplyService;
import com.spring.board.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/board/{boardId}/{commentId}/reply")
    public WriteReplyResponse writeReply(@PathVariable Long boardId, @PathVariable Long commentId,
                                         @Login User user, @RequestBody @Valid WriteReplyRequest request) {
        return replyService.writeReply(user, boardId, commentId, request);
    }

    @PatchMapping("/board/{boardId}/{commentId}/editReply/{replyId}")
    public EditReplyResponse editReply(@PathVariable Long boardId, @PathVariable Long commentId,
                                       @PathVariable Long replyId, @Login User user,
                                       @RequestBody @Valid EditReplyRequest request) {
        return replyService.editReply(user, boardId, commentId, replyId, request);
    }

    @DeleteMapping("/board/{boardId}/{commentId}/deleteReply/{replyId}")
    public void deleteReply(@PathVariable Long boardId, @PathVariable Long commentId,
                            @PathVariable Long replyId, @Login User user) {
        replyService.deleteReply(user, boardId, commentId, replyId);
    }
}
