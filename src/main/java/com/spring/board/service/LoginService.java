package com.spring.board.service;

import com.spring.board.domain.User;
import com.spring.board.exception.user.UserNotFound;
import com.spring.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    public User login(String loginId, String password) {
        return userRepository.findByLoginId(loginId)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(UserNotFound::new);
    }
}
