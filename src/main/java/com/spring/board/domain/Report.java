package com.spring.board.domain;

import com.spring.board.exception.report.DuplicationReportException;
import com.spring.board.exception.report.ReportInvalidRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Optional;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Report {

    @Id
    @GeneratedValue
    @Column(name = "report_id")
    private Long id;

    private String reportContent;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    public Report(String reportContent, User user, Board board, Optional<Report> report) {
        if (!report.isEmpty()) {
            throw new DuplicationReportException();
        }

        if (user.getId().equals(board.getUser().getId())) {
            throw new ReportInvalidRequest();
        }

        this.reportContent = reportContent;
        this.setUser(user);
        this.setBoard(board);
        user.getReports().add(this);
    }
}
