package com.spring.board.response.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BoardSearchResponse {

    private final String userNickname;
    private final Long boardId;
    private final String title;
    private final String content;

}
