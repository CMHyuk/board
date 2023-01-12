package com.spring.board.response.like;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LikeResponse {

    private final String userNickname;
    private final String boardUserNickname;
    private final String title;
    private final String content;

    @Builder
    public LikeResponse(String userNickname, String boardUserNickname, String title, String content) {
        this.userNickname = userNickname;
        this.boardUserNickname = boardUserNickname;
        this.title = title;
        this.content = content;
    }
}
