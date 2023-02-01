package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.Like;
import com.spring.board.domain.User;
import com.spring.board.exception.InvalidRequest;
import com.spring.board.exception.user.DuplicationLoginIdException;
import com.spring.board.exception.user.UserNotFound;
import com.spring.board.repository.LikeRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.user.EditUserRequest;
import com.spring.board.request.user.SaveUserRequest;
import com.spring.board.response.user.SaveUserResponse;
import com.spring.board.response.user.UserBoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.spring.board.domain.Grade.SILVER;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    @Transactional(readOnly = true)
    public List<UserBoardResponse> getUserBoards(Long id) {
        User user = userRepository.findUserWithBoards(id)
                .orElseThrow(UserNotFound::new);

        List<Board> boards = user.getBoards();

        return boards.stream()
                .map(u -> new UserBoardResponse(u.getId(), u.getTitle(), u.getContent()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserBoardResponse> getLikeBoards(Long userId) {
        List<Like> boards = likeRepository.findByUserId(userId);
        return boards.stream()
                .map(l -> new UserBoardResponse(l.getBoard().getId(), l.getBoard().getTitle(), l.getBoard().getContent()))
                .collect(Collectors.toList());
    }

    public SaveUserResponse save(SaveUserRequest request) {
        User user = User.builder()
                .nickname(request.getNickname())
                .loginId(request.getLoginId())
                .password(request.getPassword())
                .grade(SILVER)
                .build();

        validateDuplicationLoginId(user);

        User savedUser = userRepository.save(user);

        return SaveUserResponse.builder()
                .id(savedUser.getId())
                .nickname(savedUser.getNickname())
                .loginId(savedUser.getLoginId())
                .password(savedUser.getPassword())
                .build();
    }

    private void validateDuplicationLoginId(User user) {
        Optional<User> findUser = userRepository.findByLoginId(user.getLoginId());
        if (!findUser.isEmpty()) {
            throw new DuplicationLoginIdException();
        }
    }

    public User editUserPassword(Long id, EditUserRequest request, User user) {
        User findUser = checkSameUser(id, user);
        findUser.setPassword(request.getPassword());
        return findUser;
    }

    public void deleteUser(Long id, User user) {
        User findUser = checkSameUser(id, user);
        userRepository.delete(findUser);
    }

    private User checkSameUser(Long id, User user) {
        User findUser = userRepository.findById(id)
                .orElseThrow(UserNotFound::new);

        if (!findUser.getId().equals(user.getId())) {
            throw new InvalidRequest();
        }

        return findUser;
    }
}
