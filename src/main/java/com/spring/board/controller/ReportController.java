package com.spring.board.controller;

import com.spring.board.domain.User;
import com.spring.board.request.ReportRequest;
import com.spring.board.response.report.ReportBoardsResponse;
import com.spring.board.response.report.ReportResponse;
import com.spring.board.service.ReportService;
import com.spring.board.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/reportBoards")
    public List<ReportBoardsResponse> getReportBoards(@PageableDefault(sort = "id", direction = DESC) Pageable pageable) {
        return reportService.getReportBoards(pageable);
    }

    @PostMapping("/board/{boardId}/report")
    public ReportResponse reportBoard(@PathVariable Long boardId, @Login User user,
                                      @RequestBody @Valid ReportRequest request) {
        return reportService.reportBoard(boardId, user.getId(), request);
    }
}
