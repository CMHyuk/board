package com.spring.board.response.board;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WriteBoardResponse {

    private final String title;
    private final String content;

    @Builder
    public WriteBoardResponse(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
