package com.jiho.board.springbootaws.web.dto.posts;

import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.posts.Posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsSaveRequestDto {
    private String title;
    private String content;
    private Member author;

    @Builder
    public PostsSaveRequestDto(String title, String content, Member author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public PostsSaveRequestDto addMember(Member author) {
        this.author = author;
        return this;
    }

    public Posts toEntity() {
        return Posts.builder().title(title).content(content).author(author).build();
    }
}
