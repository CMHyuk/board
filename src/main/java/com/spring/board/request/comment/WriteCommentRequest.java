package com.spring.board.request.comment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class WriteCommentRequest {

    @NotBlank(message = "댓글을 입력하세요.")
    private String comment;
}
