package com.spring.board.response.board;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardResponse {

    private final String nickname;
    private final Long boardId;
    private final String title;
    private final String content;

    @Builder
    public BoardResponse(String nickname, Long boardId, String title, String content) {
        this.nickname = nickname;
        this.boardId = boardId;
        this.title = title;
        this.content = content;
    }
}
