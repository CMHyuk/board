package com.spring.board.response.report;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportResponse {

    private final String nickname;
    private final Long boardId;
    private final String title;
    private final String content;
    private final String reportContent;

    @Builder
    public ReportResponse(String nickname, Long boardId, String title, String content, String reportContent) {
        this.nickname = nickname;
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.reportContent = reportContent;
    }
}
