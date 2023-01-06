package com.spring.board.service;

import com.spring.board.domain.User;
import com.spring.board.exception.DuplicationLoginIdException;
import com.spring.board.exception.UserNotFound;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.user.EditUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User save(User user) {
        validateDuplicationLoginId(user);
        return userRepository.save(user);
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
            throw new UserNotFound();
        }

        return findUser;
    }
}
