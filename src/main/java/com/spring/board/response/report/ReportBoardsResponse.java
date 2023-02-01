package com.spring.board.response.report;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportBoardsResponse {

    private Long reportId;
    private String reporter;
    private String reportContent;
    private Long boardId;
    private String title;
    private String content;

    @Builder
    public ReportBoardsResponse(Long reportId, String reporter, String reportContent, Long boardId, String title, String content) {
        this.reportId = reportId;
        this.reporter = reporter;
        this.reportContent = reportContent;
        this.boardId = boardId;
        this.title = title;
        this.content = content;
    }
}
