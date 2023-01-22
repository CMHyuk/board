package com.spring.board.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ReportRequest {

    @NotBlank(message = "신고 내용을 입력하세요.")
    private String reportContent;
}
