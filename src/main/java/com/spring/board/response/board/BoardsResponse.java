package com.spring.board.response.board;

import com.spring.board.response.comment.CommentDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardsResponse {

    private final String userNickname;
    private final Long boardId;
    private final String title;
    private final String content;
    private final List<CommentDto> comments;

    @Builder
    public BoardsResponse(String userNickname, Long boardId, String title, String content, List<CommentDto> comments) {
        this.userNickname = userNickname;
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.comments = comments;
    }
}
