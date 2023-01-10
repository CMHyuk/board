package com.spring.board.response.board;

import com.spring.board.response.comment.CommentDto;
import com.spring.board.response.reply.ReplyDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardSearchResponse {

    private final String userNickname;
    private final Long boardId;
    private final String title;
    private final String content;
    private final List<CommentDto> comments;
    private final List<ReplyDto> replies;

    @Builder
    public BoardSearchResponse(String userNickname, Long boardId, String title, String content,
                               List<CommentDto> comments, List<ReplyDto> replies) {
        this.userNickname = userNickname;
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.comments = comments;
        this.replies = replies;
    }
}
