package com.spring.board.controller;

import com.spring.board.domain.User;
import com.spring.board.request.comment.EditCommentRequest;
import com.spring.board.request.comment.WriteCommentRequest;
import com.spring.board.response.comment.EditCommentResponse;
import com.spring.board.response.comment.SaveCommentResponse;
import com.spring.board.service.CommentService;
import com.spring.board.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/board/{boardId}/comment")
    public SaveCommentResponse write(@PathVariable Long boardId,
                                     @RequestBody @Valid WriteCommentRequest request,
                                     @Login User user) {
        return commentService.writeComment(boardId, request, user);
    }

    @PatchMapping("/board/{boardId}/editComment/{commentId}")
    public EditCommentResponse edit(@PathVariable Long boardId, @PathVariable Long commentId,
                                  @RequestBody @Valid EditCommentRequest request,
                                  @Login User user) {
        return commentService.editComment(boardId, commentId, user, request);
    }

    @DeleteMapping("/board/{boardId}/deleteComment/{commentId}")
    public void delete(@PathVariable Long boardId, @PathVariable Long commentId, @Login User user) {
        commentService.deleteComment(boardId, commentId, user);
    }
}
