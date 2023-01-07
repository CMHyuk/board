package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.User;
import com.spring.board.exception.BoardNotFound;
import com.spring.board.exception.InvalidRequest;
import com.spring.board.repository.BoardRepository;
import com.spring.board.request.board.EditBoardRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public Board write(Board board) {
        return boardRepository.save(board);
    }

    public Board get(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);
    }

    public List<Board> getBoards() {
        return boardRepository.findAll();
    }

    public List<Board> findBySearch(String title) {
        return boardRepository.findByTitleContaining(title);
    }

    public Board editBoard(Long boardId, EditBoardRequest request, User user) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        validateSameUser(user, findBoard);

        findBoard.setTitle(request.getTitle());
        findBoard.setContent(request.getContent());

        return findBoard;
    }

    public void deleteBoard(Long boardId, User user) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);
        validateSameUser(user, findBoard);
        boardRepository.delete(findBoard);
    }

    private void validateSameUser(User user, Board findBoard) {
        User boardUser = findBoard.getUser();
        if (user.getId() != boardUser.getId()) {
            throw new InvalidRequest();
        }
    }
}
