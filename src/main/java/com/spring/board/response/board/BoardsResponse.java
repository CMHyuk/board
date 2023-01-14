package com.spring.board.response.board;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardsResponse {

    private final String userNickname;
    private final Long boardId;
    private final String title;
    private final String content;

    @Builder
    public BoardsResponse(String userNickname, Long boardId, String title, String content) {
        this.userNickname = userNickname;
        this.boardId = boardId;
        this.title = title;
        this.content = content;
    }
}
