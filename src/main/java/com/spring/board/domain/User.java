package com.spring.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.spring.board.domain.Grade.ADMIN;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = PROTECTED)
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String nickname;
    private String loginId;
    private String password;

    @Enumerated(STRING)
    private Grade grade;

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Report> reports = new ArrayList<>();

    @Builder
    public User(String nickname, String loginId, String password, Grade grade) {
        this.nickname = nickname;
        this.loginId = loginId;
        this.password = password;
        this.grade = grade;
    }

    public void upgradeLevel() {
        Grade[] grades = Grade.values();
        for (int i = 0; i < grades.length; i++) {
            if (grade == grades[i]) {
                if (i == grades.length - 1) {
                    break;
                }
                if (boards.size() >= grades[i + 1].getRequiredBoardCount() && grades[i + 1] != ADMIN) {
                    grade = grades[i + 1];
                }
                break;
            }
        }
    }
}
