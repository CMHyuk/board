package com.spring.board.response.board;

import com.spring.board.response.comment.CommentDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardResponse {

    private final String nickname;
    private final Long boardId;
    private final String title;
    private final String content;
    private final List<CommentDto> comments;

    @Builder
    public BoardResponse(String nickname, Long boardId, String title, String content, List<CommentDto> comments) {
        this.nickname = nickname;
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.comments = comments;
    }
}
