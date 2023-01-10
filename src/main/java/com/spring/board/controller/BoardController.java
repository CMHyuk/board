package com.spring.board.controller;

import com.spring.board.domain.User;
import com.spring.board.request.board.EditBoardRequest;
import com.spring.board.request.board.WriteBoardRequest;
import com.spring.board.response.board.*;
import com.spring.board.service.BoardService;
import com.spring.board.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board/{boardId}")
    public BoardResponse getBoard(@PathVariable Long boardId) {
        return boardService.get(boardId);
    }

    @GetMapping("/boards")
    public List<BoardsResponse> getBoards() {
        return boardService.getBoards();

    }

    @GetMapping("/board/search")
    public List<BoardsResponse> getBoardsBySearch(@RequestParam String title) {
        return boardService.findBySearch(title);
    }

    @PostMapping("/board/write")
    public WriteBoardResponse write(@RequestBody @Valid WriteBoardRequest request, @Login User user) {
        return boardService.write(request, user.getId());
    }

    @PatchMapping("/board/edit/{boardId}")
    public EditBoardResponse edit(@PathVariable Long boardId, @RequestBody @Valid EditBoardRequest request,
                                  @Login User user) {
        return boardService.editBoard(boardId, request, user);
    }

    @DeleteMapping("/board/delete/{boardId}")
    public void delete(@PathVariable Long boardId, @Login User user) {
        boardService.deleteBoard(boardId, user);
    }
}
