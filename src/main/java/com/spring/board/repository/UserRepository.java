package com.spring.board.repository;

import com.spring.board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    @Query(value = "select u from User u join fetch u.boards where u.id =:id")
    Optional<User> findUserWithBoards(@Param("id") Long id);

}
