package com.spring.board.response.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SaveCommentResponse {
    private final String comment;
    private final String nickname;
}
