package com.spring.board.response.board;

import com.spring.board.domain.Grade;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WriteBoardResponse {

    private final String nickname;
    private final Grade grade;
    private final String title;
    private final String content;

    @Builder
    public WriteBoardResponse(String nickname, Grade grade, String title, String content) {
        this.nickname = nickname;
        this.grade = grade;
        this.title = title;
        this.content = content;
    }
}
