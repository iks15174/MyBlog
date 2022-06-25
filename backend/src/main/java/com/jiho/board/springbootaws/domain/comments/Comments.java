package com.jiho.board.springbootaws.domain.comments;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jiho.board.springbootaws.domain.BaseTimeEntity;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.posts.Posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Comments extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member author;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Posts posts;

    @Builder
    public Comments(String content, Member author, Posts posts) {
        this.content = content;
        this.author = author;
        this.posts = posts;
    }

    public void update(String content) {
        this.content = content;
    }
}
