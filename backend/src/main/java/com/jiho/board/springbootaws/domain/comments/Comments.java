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
import com.jiho.board.springbootaws.exception.exceptions.CustomBasicException;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;

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

    public Comments(String content, Member author, Posts posts) {
        checkContentNull(content);
        this.content = content;
        this.author = author;
        this.posts = posts;
    }

    public void update(String content) {
        checkContentNull(content);
        this.content = content;
    }

    private void checkContentNull(String content) {
        if(content.isEmpty()){
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
