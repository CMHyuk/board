package com.spring.board.domain;

import com.spring.board.exception.like.DuplicationLikeException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.util.Optional;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@Entity
@Table(name = "likes")
@NoArgsConstructor(access = PROTECTED)
public class Like {

    @Id
    @GeneratedValue
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    public Like(User user, Board board, Optional<Like> like) {
        if (!like.isEmpty()) {
            throw new DuplicationLikeException();
        }
        this.user = user;
        this.board = board;
        user.getLikes().add(this);
    }
}
