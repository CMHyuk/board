package com.spring.board.controller;

import com.spring.board.domain.User;
import com.spring.board.request.board.EditBoardRequest;
import com.spring.board.request.board.WriteBoardRequest;
import com.spring.board.response.board.BoardResponse;
import com.spring.board.response.board.BoardsResponse;
import com.spring.board.response.board.EditBoardResponse;
import com.spring.board.response.board.WriteBoardResponse;
import com.spring.board.service.BoardService;
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
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board/{boardId}")
    public BoardResponse getBoard(@PathVariable Long boardId) {
        return boardService.get(boardId);
    }

    @GetMapping("/boards")
    public List<BoardsResponse> getBoards(@PageableDefault(sort = "id", direction = DESC) Pageable pageable) {
        return boardService.getBoards(pageable);
    }

    @GetMapping("/board/search")
    public List<BoardsResponse> getBoardsBySearch(@RequestParam String title, @PageableDefault(sort = "id", direction = DESC) Pageable pageable) {
        return boardService.findBySearch(title, pageable);
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
