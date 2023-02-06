package com.spring.board.response.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter

public class SaveCommentResponse {
    private final String comment;
    private final String nickname;

    @Builder
    public SaveCommentResponse(String comment, String nickname) {
        this.comment = comment;
        this.nickname = nickname;
    }
}
