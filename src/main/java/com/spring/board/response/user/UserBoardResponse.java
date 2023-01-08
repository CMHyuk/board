package com.spring.board.response.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserBoardResponse {

    private final Long boardId;
    private final String title;
    private final String content;

}
