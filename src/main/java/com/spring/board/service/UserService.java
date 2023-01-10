package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.User;
import com.spring.board.exception.DuplicationLoginIdException;
import com.spring.board.exception.InvalidRequest;
import com.spring.board.exception.UserNotFound;
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

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    //n + 1 터짐
    public List<UserBoardResponse> getUserBoards(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFound::new);

        List<Board> boards = user.getBoards();

        return boards.stream()
                .map(u -> new UserBoardResponse(u.getId(), u.getTitle(), u.getContent()))
                .collect(Collectors.toList());
    }

    public SaveUserResponse save(SaveUserRequest request) {
        User user = User.builder()
                .nickname(request.getNickname())
                .loginId(request.getLoginId())
                .password(request.getPassword())
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

        if (findUser.getId() != user.getId()) {
            throw new InvalidRequest();
        }

        return findUser;
    }
}
